package org.jasig.ssp.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class SpcPersonAttributesService extends UPortalPersonAttributesService implements InitializingBean {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(SpcPersonAttributesService.class);

    // Instance Members
    private NamedParameterJdbcTemplate jdbcTemplate;
    private DataSource dataSource;
    private String sql;
    private String columnName;

    @Required
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Required
    public void setSql(String sql) {
        this.sql = sql;
    }

    @Required
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    /**
	 * Overriding the implementation in the superclass b/c SPC needs a custom 
	 * query to answer the question.
	 */
	@Override
	public Collection<String> getCoaches() {
	    
	    LOGGER.debug("Fetching coaches");

        Map<String,String> queryMap = getCoachesQuery();
        RowCallbackHandlerImpl callback = new RowCallbackHandlerImpl(columnName);
        jdbcTemplate.query(sql, queryMap, callback);

        List<String> rslt = new ArrayList<String>(callback.getResults());
        Collections.sort(rslt);
		
		return rslt;

	}

    private static final class RowCallbackHandlerImpl implements RowCallbackHandler {
        
        // Instance Members
        private final String columnName;
        private final Set<String> rows = new HashSet<String>();
        
        public RowCallbackHandlerImpl(String columnName) {
            this.columnName = columnName;
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            rows.add(rs.getString(columnName));
        }
        
        public Set<String> getResults() {
            return rows;
        }
        
    }

}