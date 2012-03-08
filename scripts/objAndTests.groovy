// script to create the infrastructure for an object:  database, dao, model, service, controller, factory, to

// Steps:
// set paths of example objects to copy from
// check paths to see if any are missing
// for each path, copy file, replace modelname


/**
 * A file which is to be used as a Template for generating classes to support a model
 */
class ExampleFile {
	String srcPath
	List<String> appPath
	List<String> packagePath
	String modelName
	String pattern
	
	File file

	ExampleFile(String srcPath, List<String> appPath, List<String> packagePath, String modelName, String pattern){
		this.srcPath = srcPath
		this.appPath = appPath
		this.packagePath = packagePath
		this.modelName = modelName
		this.pattern = pattern
	}
	
	String getPath(){
		return pathWithModelName(modelName)
	}

	String pathWithModelName(String modelName){
		return srcPath + appPath.join("/") + "/" + packagePath.join("/") + "/" + modelName + pattern
	}

	File getFile(){
		if(!file){
			file = new File(getPath())
		}
		return file
	}

	boolean isReadable(){
	   return (getFile().exists() && file.canRead()) 
	}

	/**
	 * Is this ExampleFile the model?  Used to determine if a table should be generated.
	 */
	boolean isModel(){
		return pattern == ".java"
	}

	String toString(){
		return getPath() + " " + ((isReadable())?": accessable":"inaccessable")
	}
}

class Templater{

	Templater(boolean create, boolean overwrite, boolean dryRun, boolean displayFileContents){
		this.create = create
		this.overwrite = overwrite
		this.dryRun = dryRun
		this.displayFileContents = displayFileContents
	}

	boolean create, overwrite, dryRun, displayFileContents

	/*
	 * Set some paths to access the files
	 */
	List<String> appPath = ["edu", "sinclair", "ssp"]
	String basePath = "/data/code/infinum/javaWorkspace/ssp/"
	
	private String javaMainPath = basePath + "src/main/java/"
	private String javaTestPath = basePath + "src/test/java/"
	private String resourcesMainPath = basePath + "src/main/resources/"
	private String resourcesTestPath = basePath + "src/test/resources/"

	// The liquibase changelog to add the table.
	String liquibaseChangeLogLocation =  resourcesMainPath + appPath.join("/") + "/" + "database/00.01.0.xml"

	//Turn a string into a camel cased version of itself
	String camelCased(String val){
		StringBuilder sb = new StringBuilder()
		sb.append(val[0].toLowerCase())
		sb.append(val[1 .. val.size()-1])
		return sb.toString()
	}

	/*
	 * The model to use as an example, the new model, and the subpackage to place the files
	 * Action to perform: create the files, or delete them.
	 * Whether to just print results or actually perform them
	 */
	public void run(String modelName, String newModelName, List<String> subpackage){
		List<ExampleFile> exampleFiles = [new ExampleFile(javaMainPath, appPath, ["dao"] + subpackage, modelName, "Dao.java"),
					new ExampleFile(javaMainPath, appPath, ["model"] + subpackage, modelName, ".java"),
					new ExampleFile(javaMainPath, appPath, ["service"] + subpackage, modelName, "Service.java"),
					new ExampleFile(javaMainPath, appPath, ["service"] + subpackage + ["impl"], modelName, "ServiceImpl.java"),
					new ExampleFile(javaMainPath, appPath, ["web", "api"] + subpackage, modelName, "Controller.java"),
					new ExampleFile(javaMainPath, appPath, ["factory"] + subpackage, modelName, "TOFactory.java"),
					new ExampleFile(javaMainPath, appPath, ["factory"] + subpackage + ["impl"], modelName, "TOFactoryImpl.java"),
					new ExampleFile(javaMainPath, appPath, ["transferobject"] + subpackage, modelName, "TO.java"),
					new ExampleFile(javaTestPath, appPath, ["dao"] + subpackage, modelName, "DaoTest.java"),
					new ExampleFile(javaTestPath, appPath, ["service"] + subpackage + ["impl"], modelName, "ServiceTest.java"),
					new ExampleFile(javaTestPath, appPath, ["factory"] + subpackage + ["impl"], modelName, "TOFactoryTest.java")
					]
		
		println "\n\n"
		exampleFiles.each { exampleFile -> 
			println "\n" + exampleFile
			
			if(!exampleFile.isReadable()){
				return
			}

			String newFileName = exampleFile.pathWithModelName(newModelName)
			File newFile = new File(newFileName)
	
			if(create){
				if(newFile.exists() && !overwrite){
					println "The file $newFileName already exists, skipping"
				}else{
					if(newFile.exists() ){
						newFile.text = ""
					}
					println "Writing $newFileName"
					exampleFile.getFile().eachLine { String line ->
						String newLine = line.replaceAll(modelName, newModelName)
						newLine = newLine.replaceAll(camelCased(modelName), camelCased(newModelName))
						if(dryRun){
							if(displayFileContents){
								println newLine 
							}
						}else{
							newFile.append(newLine + "\n")
						}
					}
					
					if(exampleFile.isModel()){
						createTableForModel(newModelName)
					}
				}
			}else{
				if(newFile.exists()){
					println "Deleting " + newFileName
					if(!dryRun){
						newFile.delete()
					}
				}else{
					println "File did not exist, cannot delete"
				}
			}
		}   
	}

	public void createTableForModel(String newModelName){
		File lcl = new File(liquibaseChangeLogLocation)
		if(!lcl.canWrite()) {
			println "Cannot write to liquibaseChangeLog"
			return
		}

		StringBuilder newChangeLog = new StringBuilder()
		
		//Remove the last line
		lcl.eachLine(){
			if(!it.contains('</databaseChangeLog>')){
				newChangeLog.append(it)
				newChangeLog.append("\n")
			}
		}

		//add the table
		newChangeLog.append """
	<changeSet author='daniel.bower' id='Add ${newModelName} table'>
		<createTable tableName="${newModelName}">
			<column name="id" type="uuid">
				<constraints primaryKey="true" nullable="false" />
			</column>
			<column name="name" type="character varying(25)">
				<constraints nullable="false" />
			</column>
			<column name="description" type="character varying(150)">
				<constraints nullable="true" />
			</column>
			<column name="created_date" type="datetime">
				<constraints nullable="false"/>
			</column>
			<column name="modified_date" type="datetime"/>
			<column name="created_by" type="uuid">
				<constraints nullable="false" 
					foreignKeyName="${newModelName}_created_by_person_id"
					references="person(id)"/>
			</column>
			<column name="modified_by" type="uuid">
				<constraints nullable="true" foreignKeyName="${newModelName}_modified_by_person_id"
					references="person(id)"/>
			</column>
			<column name="object_status" type="int">
				<constraints nullable="false" />
			</column>
		</createTable>
		
		<sql>grant all on challenge to ssp</sql>
		<rollback>
			<dropTable tableName="${newModelName}"/>
		</rollback>
		
		<!-- Theres a different assumption in the liquibase handling of timezones on postgres.
		Specifying "Without" timezone-->
		<modifySql dbms="postgresql">
			<replace replace="WITH TIME ZONE" with="WITHOUT TIME ZONE"/>
		</modifySql>

	</changeSet> \n """

		newChangeLog.append '</databaseChangeLog>'

		//Write the file
		lcl.text = newChangeLog.toString()
	} 
}


class ReferenceDataTemplater {
	String templateModel = "Challenge"
	List<String> subpackage = ["reference"]
	List<String> referenceDataModels = ["ChildCareArrangement", "Citizenship", "EducationGoal", "EducationLevel", "Ethnicity", "FundingSource", "MaritalStatus", "StudentStatus", "VeteranStatus"]

	public void run(boolean create, boolean overwrite, boolean dryRun, boolean displayFileContents){
		Templater templater = new Templater(create, overwrite, dryRun, displayFileContents)
		referenceDataModels.each{
			templater.run(templateModel, it, subpackage)
		}
	}	
}


new ReferenceDataTemplater().run(true, true, true, false)
