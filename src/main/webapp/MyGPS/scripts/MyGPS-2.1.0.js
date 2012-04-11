(function() {
  var AbstractService, AbstractSessionViewModel, AbstractTasksViewModel, Challenge, ChallengeReferral, ChallengeReferralSearchViewModel, ChallengeReferralService, ChallengeService, ContactViewModel, Form, FormOption, FormQuestion, FormSection, HomeViewModel, LoginViewModel, MessageService, Person, SelfHelpGuide, SelfHelpGuideContent, SelfHelpGuideQuestion, SelfHelpGuideResponse, SelfHelpGuideResponseService, SelfHelpGuideService, SelfHelpGuideViewModel, SelfHelpGuidesViewModel, Session, SessionService, StudentIntakeService, StudentIntakeViewModel, Task, TaskFilter, TaskService,
    __hasProp = Object.prototype.hasOwnProperty,
    __extends = function(child, parent) { for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; } function ctor() { this.constructor = child; } ctor.prototype = parent.prototype; child.prototype = new ctor; child.__super__ = parent.prototype; return child; };

  namespace("mygps.enumeration", {
    TaskFilter: TaskFilter = (function() {

      function TaskFilter(name, filterFunction) {
        this.name = name;
        this.filterFunction = filterFunction;
      }

      TaskFilter.COMPLETED = new TaskFilter("Completed", function(task) {
        return task.completed();
      });

      TaskFilter.ALL = new TaskFilter("All", function(task) {
        return true;
      });

      TaskFilter.ACTIVE = new TaskFilter("Active", function(task) {
        return !task.completed();
      });

      TaskFilter.enumerators = function() {
        return [TaskFilter.ALL, TaskFilter.ACTIVE, TaskFilter.COMPLETED];
      };

      return TaskFilter;

    })()
  });

  namespace('mygps.model', {
    Challenge: Challenge = (function() {

      function Challenge(id, name, description, referralCount) {
        this.id = ko.observable(id);
        this.name = ko.observable(name);
        this.description = ko.observable(description);
        this.referralCount = ko.observable(referralCount);
        this.challengeReferrals = ko.observableArray();
      }

      Challenge.createFromTransferObject = function(challengeTO) {
        return new Challenge(challengeTO.id, challengeTO.name, challengeTO.description, challengeTO.referralCount);
      };

      return Challenge;

    })()
  });

  namespace('mygps.model', {
    ChallengeReferral: ChallengeReferral = (function() {

      function ChallengeReferral(id, name, description, details) {
        this.id = ko.observable(id);
        this.name = ko.observable(name);
        this.description = ko.observable(description);
        this.details = ko.observable(details);
      }

      ChallengeReferral.createFromTransferObject = function(challengeReferralTO) {
        return new ChallengeReferral(challengeReferralTO.id, challengeReferralTO.name, challengeReferralTO.description, challengeReferralTO.details);
      };

      return ChallengeReferral;

    })()
  });

  namespace('mygps.model', {
    Form: Form = (function() {

      function Form(id, label, sections) {
        var section, _i, _len, _ref;
        this.id = ko.observable(id);
        this.label = ko.observable(label);
        this.sections = ko.observableArray(sections);
        this.valid = ko.dependentObservable(this.evaluateValid, this);
        _ref = this.sections();
        for (_i = 0, _len = _ref.length; _i < _len; _i++) {
          section = _ref[_i];
          section.form(this);
        }
      }

      Form.prototype.evaluateValid = function() {
        return !_.any(this.sections(), function(section) {
          return !section.valid();
        });
      };

      Form.prototype.validate = function() {
        var section, _i, _len, _ref;
        _ref = this.sections();
        for (_i = 0, _len = _ref.length; _i < _len; _i++) {
          section = _ref[_i];
          section.validate();
        }
      };

      Form.createFromTransferObject = function(formTO) {
        var section, sections;
        if (formTO.sections != null) {
          sections = (function() {
            var _i, _len, _ref, _results;
            _ref = formTO.sections;
            _results = [];
            for (_i = 0, _len = _ref.length; _i < _len; _i++) {
              section = _ref[_i];
              _results.push(mygps.model.FormSection.createFromTransferObject(section));
            }
            return _results;
          })();
        }
        return new Form(formTO.id, formTO.label, sections);
      };

      Form.toTransferObject = function(form) {
        var formTO, section, sections;
        if (form.sections()) {
          sections = (function() {
            var _i, _len, _ref, _results;
            _ref = form.sections();
            _results = [];
            for (_i = 0, _len = _ref.length; _i < _len; _i++) {
              section = _ref[_i];
              _results.push(mygps.model.FormSection.toTransferObject(section));
            }
            return _results;
          })();
        }
        formTO = {
          id: form.id(),
          label: form.label(),
          sections: sections
        };
        return formTO;
      };

      return Form;

    })()
  });

  namespace('mygps.model', {
    FormOption: FormOption = (function() {

      function FormOption(id, label, value) {
        this.id = ko.observable(id);
        this.label = ko.observable(label);
        this.value = ko.observable(value);
      }

      FormOption.createFromTransferObject = function(formOptionTO) {
        return new FormOption(formOptionTO.id, formOptionTO.label, formOptionTO.value);
      };

      FormOption.toTransferObject = function(formOption) {
        var formOptionTO;
        formOptionTO = {
          id: formOption.id(),
          label: formOption.label(),
          value: formOption.value()
        };
        return formOptionTO;
      };

      return FormOption;

    })()
  });

  namespace('mygps.model', {
    FormQuestion: FormQuestion = (function() {

      function FormQuestion(id, label, type, value, values, options, readOnly, required, maximumLength, visibilityExpression, availabilityExpression, validationExpression) {
        this.id = ko.observable(id);
        this.label = ko.observable(label);
        this.type = ko.observable(type);
        this.value = ko.observable(value);
        this.values = ko.observableArray(values || []);
        this.options = ko.observableArray(options || []);
        this.readOnly = ko.observable(readOnly);
        this.required = ko.observable(required);
        this.maximumLength = ko.observable(maximumLength);
        this.visibilityExpression = ko.observable(visibilityExpression);
        this.availabilityExpression = ko.observable(availabilityExpression);
        this.validationExpression = ko.observable(validationExpression);
        this.labelWithIndicator = ko.observable(required ? label + ' *' : label);
        this.section = ko.observable(null);
        this.valid = ko.observable(true);
        this.enabled = ko.dependentObservable(this.evaluateEnabled, this);
        this.visible = ko.dependentObservable(this.evaluateVisible, this);
      }

      FormQuestion.prototype.evaluateEnabled = function() {
        if (this.readOnly()) return false;
        if (this.availabilityExpression() != null) {
          return this.executeExpression(this.availabilityExpression());
        }
        return true;
      };

      FormQuestion.prototype.evaluateVisible = function() {
        if (this.visibilityExpression() != null) {
          return this.executeExpression(this.visibilityExpression());
        }
        return true;
      };

      FormQuestion.prototype.executeExpression = function(expression) {
        var _this = this;
        window['getQuestionById'] = function(id) {
          var _ref;
          return _.detect((_ref = _this.section()) != null ? _ref.questions() : void 0, function(question) {
            return question.id() === id;
          });
        };
        window['hasValueForQuestionId'] = function(value, questionId) {
          var question;
          question = getQuestionById(questionId);
          if (question != null) {
            if (question.type() === "checklist") {
              return _.include(question.values(), value);
            } else {
              return question.value() === value;
            }
          }
          return false;
        };
        window['hasValue'] = function(expectedValue) {
          return _this.value() === expectedValue;
        };
        window['isChecked'] = function() {
          if (_this.value()) {
            return true;
          } else {
            return false;
          }
        };
        return eval(expression);
      };

      FormQuestion.prototype.validate = function() {
        var valid;
        valid = true;
        if (this.enabled() && this.visible()) {
          if (this.required() && (this.value() === null || this.value() === "")) {
            valid = false;
          }
          if (valid && (this.validationExpression() != null)) {
            valid = this.executeExpression(this.validationExpression());
          }
        }
        this.valid(valid);
      };

      FormQuestion.createFromTransferObject = function(formQuestionTO) {
        var option, options;
        if (formQuestionTO.options != null) {
          options = (function() {
            var _i, _len, _ref, _results;
            _ref = formQuestionTO.options;
            _results = [];
            for (_i = 0, _len = _ref.length; _i < _len; _i++) {
              option = _ref[_i];
              _results.push(mygps.model.FormOption.createFromTransferObject(option));
            }
            return _results;
          })();
        }
        return new FormQuestion(formQuestionTO.id, formQuestionTO.label, formQuestionTO.type, formQuestionTO.value, formQuestionTO.values, options, formQuestionTO.readOnly, formQuestionTO.required, formQuestionTO.maximumLength, formQuestionTO.visibilityExpression, formQuestionTO.availabilityExpression, formQuestionTO.validationExpression);
      };

      FormQuestion.toTransferObject = function(formQuestion) {
        var formQuestionTO, option, options;
        if (formQuestion.options()) {
          options = (function() {
            var _i, _len, _ref, _results;
            _ref = formQuestion.options();
            _results = [];
            for (_i = 0, _len = _ref.length; _i < _len; _i++) {
              option = _ref[_i];
              _results.push(mygps.model.FormOption.toTransferObject(option));
            }
            return _results;
          })();
        }
        formQuestionTO = {
          id: formQuestion.id(),
          label: formQuestion.label(),
          type: formQuestion.type(),
          value: formQuestion.value(),
          values: formQuestion.values(),
          options: options,
          readOnly: formQuestion.readOnly(),
          required: formQuestion.required(),
          maximumLength: formQuestion.maximumLength(),
          visibilityExpression: formQuestion.visibilityExpression(),
          availabilityExpression: formQuestion.availabilityExpression(),
          validationExpression: formQuestion.validationExpression()
        };
        return formQuestionTO;
      };

      return FormQuestion;

    })()
  });

  namespace('mygps.model', {
    FormSection: FormSection = (function() {

      function FormSection(id, label, questions) {
        var question, _i, _len, _ref;
        this.id = ko.observable(id);
        this.label = ko.observable(label);
        this.questions = ko.observableArray(questions);
        this.valid = ko.dependentObservable(this.evaluateValid, this);
        this.form = ko.observable(null);
        _ref = this.questions();
        for (_i = 0, _len = _ref.length; _i < _len; _i++) {
          question = _ref[_i];
          question.section(this);
        }
      }

      FormSection.prototype.evaluateValid = function() {
        return !_.any(this.questions(), function(question) {
          return !question.valid();
        });
      };

      FormSection.prototype.validate = function() {
        var question, _i, _len, _ref;
        _ref = this.questions();
        for (_i = 0, _len = _ref.length; _i < _len; _i++) {
          question = _ref[_i];
          question.validate();
        }
      };

      FormSection.createFromTransferObject = function(formSectionTO) {
        var question, questions;
        if (formSectionTO.questions != null) {
          questions = (function() {
            var _i, _len, _ref, _results;
            _ref = formSectionTO.questions;
            _results = [];
            for (_i = 0, _len = _ref.length; _i < _len; _i++) {
              question = _ref[_i];
              _results.push(mygps.model.FormQuestion.createFromTransferObject(question));
            }
            return _results;
          })();
        }
        return new FormSection(formSectionTO.id, formSectionTO.label, questions);
      };

      FormSection.toTransferObject = function(formSection) {
        var formSectionTO, question, questions;
        if (formSection.questions()) {
          questions = (function() {
            var _i, _len, _ref, _results;
            _ref = formSection.questions();
            _results = [];
            for (_i = 0, _len = _ref.length; _i < _len; _i++) {
              question = _ref[_i];
              _results.push(mygps.model.FormQuestion.toTransferObject(question));
            }
            return _results;
          })();
        }
        formSectionTO = {
          id: formSection.id(),
          label: formSection.label(),
          questions: questions
        };
        return formSectionTO;
      };

      return FormSection;

    })()
  });

  namespace('mygps.model', {
    Person: Person = (function() {

      function Person(id, firstName, lastName, phoneNumber, emailAddress, photoURL, coach) {
        this.id = ko.observable(id);
        this.firstName = ko.observable(firstName);
        this.lastName = ko.observable(lastName);
        this.phoneNumber = ko.observable(phoneNumber);
        this.photoURL = ko.observable(photoURL);
        this.coach = ko.observable(coach);
      }

      Person.createFromTransferObject = function(personTO) {
        var coach;
        if (personTO.coach != null) {
          coach = mygps.model.Person.createFromTransferObject(personTO.coach);
        }
        return new Person(personTO.id, personTO.firstName, personTO.lastName, personTO.phoneNumber, personTO.emailAddress, personTO.photoURL, coach);
      };

      return Person;

    })()
  });

  namespace('mygps.model', {
    SelfHelpGuide: SelfHelpGuide = (function() {

      function SelfHelpGuide(id, name, description) {
        this.id = ko.observable(id);
        this.name = ko.observable(name);
        this.description = ko.observable(description);
      }

      SelfHelpGuide.createFromTransferObject = function(selfHelpGuideTO) {
        return new SelfHelpGuide(selfHelpGuideTO.id, selfHelpGuideTO.name, selfHelpGuideTO.description);
      };

      return SelfHelpGuide;

    })()
  });

  namespace('mygps.model', {
    SelfHelpGuideContent: SelfHelpGuideContent = (function(_super) {

      __extends(SelfHelpGuideContent, _super);

      function SelfHelpGuideContent(id, name, description, introductoryText, questions) {
        SelfHelpGuideContent.__super__.constructor.call(this, id, name, description);
        this.introductoryText = ko.observable(introductoryText);
        this.questions = ko.observableArray(questions);
      }

      SelfHelpGuideContent.createFromTransferObject = function(selfHelpGuideContentTO) {
        var question, questions;
        if (selfHelpGuideContentTO.questions != null) {
          questions = (function() {
            var _i, _len, _ref, _results;
            _ref = selfHelpGuideContentTO.questions;
            _results = [];
            for (_i = 0, _len = _ref.length; _i < _len; _i++) {
              question = _ref[_i];
              _results.push(mygps.model.SelfHelpGuideQuestion.createFromTransferObject(question));
            }
            return _results;
          })();
        }
        return new SelfHelpGuideContent(selfHelpGuideContentTO.id, selfHelpGuideContentTO.name, selfHelpGuideContentTO.description, selfHelpGuideContentTO.introductoryText, questions);
      };

      return SelfHelpGuideContent;

    })(SelfHelpGuide)
  });

  namespace('mygps.model', {
    SelfHelpGuideQuestion: SelfHelpGuideQuestion = (function() {

      function SelfHelpGuideQuestion(id, headingText, descriptionText, questionText, mandatory) {
        this.id = ko.observable(id);
        this.headingText = ko.observable(headingText);
        this.descriptionText = ko.observable(descriptionText);
        this.questionText = ko.observable(questionText);
        this.mandatory = ko.observable(mandatory);
        this.response = ko.observable(null);
      }

      SelfHelpGuideQuestion.createFromTransferObject = function(selfHelpGuideQuestionTO) {
        return new SelfHelpGuideQuestion(selfHelpGuideQuestionTO.id, selfHelpGuideQuestionTO.headingText, selfHelpGuideQuestionTO.descriptionText, selfHelpGuideQuestionTO.questionText, selfHelpGuideQuestionTO.mandatory);
      };

      return SelfHelpGuideQuestion;

    })()
  });

  namespace('mygps.model', {
    SelfHelpGuideResponse: SelfHelpGuideResponse = (function() {

      function SelfHelpGuideResponse(id, summaryText, challengesIdentified, triggeredEarlyAlert) {
        this.id = ko.observable(id);
        this.summaryText = ko.observable(summaryText);
        this.challengesIdentified = ko.observable(challengesIdentified);
        this.triggeredEarlyAlert = ko.observable(triggeredEarlyAlert);
      }

      SelfHelpGuideResponse.createFromTransferObject = function(selfHelpGuideResponseTO) {
        var challengeIdentified, challengesIdentified;
        if (selfHelpGuideResponseTO.challengesIdentified != null) {
          challengesIdentified = (function() {
            var _i, _len, _ref, _results;
            _ref = selfHelpGuideResponseTO.challengesIdentified;
            _results = [];
            for (_i = 0, _len = _ref.length; _i < _len; _i++) {
              challengeIdentified = _ref[_i];
              _results.push(mygps.model.Challenge.createFromTransferObject(challengeIdentified));
            }
            return _results;
          })();
        }
        return new SelfHelpGuideResponse(selfHelpGuideResponseTO.id, selfHelpGuideResponseTO.summaryText, challengesIdentified, selfHelpGuideResponseTO.triggeredEarlyAlert);
      };

      return SelfHelpGuideResponse;

    })()
  });

  namespace('mygps.model', {
    Task: Task = (function() {

      function Task(id, type, name, description, details, dueDate, completed, deletable, challengeId, challengeReferralId) {
        this.id = ko.observable(id);
        this.type = ko.observable(type);
        this.name = ko.observable(name);
        this.description = ko.observable(description);
        this.details = ko.observable(details);
        this.dueDate = ko.observable(dueDate);
        this.completed = ko.observable(completed);
        this.deletable = ko.observable(deletable);
        this.challengeId = ko.observable(challengeId);
        this.challengeReferralId = ko.observable(challengeReferralId);
      }

      Task.createFromTransferObject = function(taskTO) {
        var parseDate;
        parseDate = function(msSinceEpoch) {
          if (msSinceEpoch != null) {
            return new Date(msSinceEpoch);
          } else {
            return null;
          }
        };
        return new Task(taskTO.id, taskTO.type, taskTO.name, taskTO.description, taskTO.details, parseDate(taskTO.dueDate), taskTO.completed, taskTO.deletable, taskTO.challengeId, taskTO.challengeReferralId);
      };

      return Task;

    })()
  });

  namespace('mygps.session', {
    Session: Session = (function() {

      function Session(sessionService) {
        var _this = this;
        this.sessionService = sessionService;
        this.authenticatedPerson = ko.observable(null);
        this.initialized = ko.observable(false);
        this.sessionService.getAuthenticatedPerson({
          result: function(result) {
            _this.authenticatedPerson(result);
            return _this.initialized(true);
          },
          fault: function(fault) {
            alert(fault.responseText);
            return _this.initialized(true);
          }
        });
      }

      return Session;

    })()
  });

  namespace('mygps.service', {
    AbstractService: AbstractService = (function() {

      function AbstractService(baseURL) {
        this.baseURL = baseURL;
      }

      AbstractService.prototype.createURL = function(value) {
        var _ref;
        return "" + ((_ref = this.baseURL) != null ? _ref : '') + value;
      };

      return AbstractService;

    })()
  });

  namespace('mygps.service', {
    ChallengeReferralService: ChallengeReferralService = (function(_super) {

      __extends(ChallengeReferralService, _super);

      function ChallengeReferralService(baseURL) {
        ChallengeReferralService.__super__.constructor.call(this, baseURL);
      }

      ChallengeReferralService.prototype.getByChallengeId = function(challengeId, callbacks) {
        return $.ajax({
          url: this.createURL("/getByChallengeId?challengeId=" + challengeId),
          dataType: "json",
          success: function(result) {
            var challengeReferral, challengeReferrals;
            challengeReferrals = (function() {
              var _i, _len, _results;
              _results = [];
              for (_i = 0, _len = result.length; _i < _len; _i++) {
                challengeReferral = result[_i];
                _results.push(mygps.model.ChallengeReferral.createFromTransferObject(challengeReferral));
              }
              return _results;
            })();
            return callbacks != null ? typeof callbacks.result === "function" ? callbacks.result(challengeReferrals) : void 0 : void 0;
          },
          error: function(fault) {
            return callbacks != null ? typeof callbacks.fault === "function" ? callbacks.fault(fault) : void 0 : void 0;
          }
        });
      };

      ChallengeReferralService.prototype.search = function(challengeId, query, callbacks) {
        return $.ajax({
          url: this.createURL("/search?challengeId=" + challengeId + "&query=" + query),
          dataType: "json",
          success: function(result) {
            var challengeReferral, challengeReferrals;
            challengeReferrals = (function() {
              var _i, _len, _results;
              _results = [];
              for (_i = 0, _len = result.length; _i < _len; _i++) {
                challengeReferral = result[_i];
                _results.push(mygps.model.ChallengeReferral.createFromTransferObject(challengeReferral));
              }
              return _results;
            })();
            return callbacks != null ? typeof callbacks.result === "function" ? callbacks.result(challengeReferrals) : void 0 : void 0;
          },
          error: function(fault) {
            return callbacks != null ? typeof callbacks.fault === "function" ? callbacks.fault(fault) : void 0 : void 0;
          }
        });
      };

      return ChallengeReferralService;

    })(mygps.service.AbstractService)
  });

  namespace('mygps.service', {
    ChallengeService: ChallengeService = (function(_super) {

      __extends(ChallengeService, _super);

      function ChallengeService(baseURL) {
        ChallengeService.__super__.constructor.call(this, baseURL);
      }

      ChallengeService.prototype.search = function(query, callbacks) {
        return $.ajax({
          url: this.createURL("/search?query=" + query),
          dataType: "json",
          success: function(result) {
            var challenge, challenges;
            challenges = (function() {
              var _i, _len, _results;
              _results = [];
              for (_i = 0, _len = result.length; _i < _len; _i++) {
                challenge = result[_i];
                _results.push(mygps.model.Challenge.createFromTransferObject(challenge));
              }
              return _results;
            })();
            return callbacks != null ? typeof callbacks.result === "function" ? callbacks.result(challenges) : void 0 : void 0;
          },
          error: function(fault) {
            return callbacks != null ? typeof callbacks.fault === "function" ? callbacks.fault(fault) : void 0 : void 0;
          }
        });
      };

      return ChallengeService;

    })(mygps.service.AbstractService)
  });

  namespace('mygps.service', {
    MessageService: MessageService = (function(_super) {

      __extends(MessageService, _super);

      function MessageService(baseURL) {
        MessageService.__super__.constructor.call(this, baseURL);
      }

      MessageService.prototype.contactCoach = function(subject, message, callbacks) {
        return $.ajax({
          url: this.createURL(""),
          type: "POST",
          data: JSON.stringify({
            subject: subject,
            message: message
          }),
          contentType: "application/json",
          success: function(result) {
            return callbacks != null ? typeof callbacks.result === "function" ? callbacks.result(result) : void 0 : void 0;
          },
          error: function(fault) {
            return callbacks != null ? typeof callbacks.fault === "function" ? callbacks.fault(fault) : void 0 : void 0;
          }
        });
      };

      return MessageService;

    })(mygps.service.AbstractService)
  });

  namespace('mygps.service', {
    SelfHelpGuideResponseService: SelfHelpGuideResponseService = (function(_super) {

      __extends(SelfHelpGuideResponseService, _super);

      function SelfHelpGuideResponseService(baseURL) {
        SelfHelpGuideResponseService.__super__.constructor.call(this, baseURL);
      }

      SelfHelpGuideResponseService.prototype.cancel = function(selfHelpGuideResponseId, callbacks) {
        return $.ajax({
          url: this.createURL("/cancel?selfHelpGuideResponseId=" + selfHelpGuideResponseId),
          dataType: "json",
          success: function(result) {
            return callbacks != null ? typeof callbacks.result === "function" ? callbacks.result(result) : void 0 : void 0;
          },
          error: function(fault) {
            return callbacks != null ? typeof callbacks.fault === "function" ? callbacks.fault(fault) : void 0 : void 0;
          }
        });
      };

      SelfHelpGuideResponseService.prototype.complete = function(selfHelpGuideResponseId, callbacks) {
        return $.ajax({
          url: this.createURL("/complete?selfHelpGuideResponseId=" + selfHelpGuideResponseId),
          dataType: "json",
          success: function(result) {
            return callbacks != null ? typeof callbacks.result === "function" ? callbacks.result(result) : void 0 : void 0;
          },
          error: function(fault) {
            return callbacks != null ? typeof callbacks.fault === "function" ? callbacks.fault(fault) : void 0 : void 0;
          }
        });
      };

      SelfHelpGuideResponseService.prototype.getById = function(selfHelpGuideResponseId, callbacks) {
        return $.ajax({
          url: this.createURL("/getById?selfHelpGuideResponseId=" + selfHelpGuideResponseId),
          dataType: "json",
          success: function(result) {
            var selfHelpGuideResponse;
            selfHelpGuideResponse = mygps.model.SelfHelpGuideResponse.createFromTransferObject(result);
            return callbacks != null ? typeof callbacks.result === "function" ? callbacks.result(selfHelpGuideResponse) : void 0 : void 0;
          },
          error: function(fault) {
            return callbacks != null ? typeof callbacks.fault === "function" ? callbacks.fault(fault) : void 0 : void 0;
          }
        });
      };

      SelfHelpGuideResponseService.prototype.initiate = function(selfHelpGuideId, callbacks) {
        return $.ajax({
          url: this.createURL("/initiate?selfHelpGuideId=" + selfHelpGuideId),
          dataType: "json",
          success: function(result) {
            return callbacks != null ? typeof callbacks.result === "function" ? callbacks.result(result) : void 0 : void 0;
          },
          error: function(fault) {
            return callbacks != null ? typeof callbacks.fault === "function" ? callbacks.fault(fault) : void 0 : void 0;
          }
        });
      };

      SelfHelpGuideResponseService.prototype.answer = function(selfHelpGuideResponseId, selfHelpGuideQuestionId, response, callbacks) {
        return $.ajax({
          url: this.createURL("/answer?selfHelpGuideResponseId=" + selfHelpGuideResponseId + "&selfHelpGuideQuestionId=" + selfHelpGuideQuestionId + "&response=" + response),
          dataType: "json",
          success: function(result) {
            return callbacks != null ? typeof callbacks.result === "function" ? callbacks.result(result) : void 0 : void 0;
          },
          error: function(fault) {
            return callbacks != null ? typeof callbacks.fault === "function" ? callbacks.fault(fault) : void 0 : void 0;
          }
        });
      };

      return SelfHelpGuideResponseService;

    })(mygps.service.AbstractService)
  });

  namespace('mygps.service', {
    SelfHelpGuideService: SelfHelpGuideService = (function(_super) {

      __extends(SelfHelpGuideService, _super);

      function SelfHelpGuideService(baseURL) {
        SelfHelpGuideService.__super__.constructor.call(this, baseURL);
      }

      SelfHelpGuideService.prototype.getAll = function(callbacks) {
        return $.ajax({
          url: this.createURL("/getAll"),
          dataType: "json",
          success: function(result) {
            var selfHelpGuide, selfHelpGuides;
            selfHelpGuides = (function() {
              var _i, _len, _results;
              _results = [];
              for (_i = 0, _len = result.length; _i < _len; _i++) {
                selfHelpGuide = result[_i];
                _results.push(mygps.model.SelfHelpGuide.createFromTransferObject(selfHelpGuide));
              }
              return _results;
            })();
            return callbacks != null ? typeof callbacks.result === "function" ? callbacks.result(selfHelpGuides) : void 0 : void 0;
          },
          error: function(fault) {
            return callbacks != null ? typeof callbacks.fault === "function" ? callbacks.fault(fault) : void 0 : void 0;
          }
        });
      };

      SelfHelpGuideService.prototype.getBySelfHelpGuideGroup = function(selfHelpGuideGroupId, callbacks) {
        return $.ajax({
          url: this.createURL("/getBySelfHelpGuideGroup?selfHelpGuideGroupId=" + selfHelpGuideGroupId),
          dataType: "json",
          success: function(result) {
            var selfHelpGuide, selfHelpGuides;
            selfHelpGuides = (function() {
              var _i, _len, _results;
              _results = [];
              for (_i = 0, _len = result.length; _i < _len; _i++) {
                selfHelpGuide = result[_i];
                _results.push(mygps.model.SelfHelpGuide.createFromTransferObject(selfHelpGuide));
              }
              return _results;
            })();
            return callbacks != null ? typeof callbacks.result === "function" ? callbacks.result(selfHelpGuides) : void 0 : void 0;
          },
          error: function(fault) {
            return callbacks != null ? typeof callbacks.fault === "function" ? callbacks.fault(fault) : void 0 : void 0;
          }
        });
      };

      SelfHelpGuideService.prototype.getContentById = function(selfHelpGuideId, callbacks) {
        return $.ajax({
          url: this.createURL("/getContentById?selfHelpGuideId=" + selfHelpGuideId),
          dataType: "json",
          success: function(result) {
            var selfHelpGuideContent;
            selfHelpGuideContent = mygps.model.SelfHelpGuideContent.createFromTransferObject(result);
            return callbacks != null ? typeof callbacks.result === "function" ? callbacks.result(selfHelpGuideContent) : void 0 : void 0;
          },
          error: function(fault) {
            return callbacks != null ? typeof callbacks.fault === "function" ? callbacks.fault(fault) : void 0 : void 0;
          }
        });
      };

      return SelfHelpGuideService;

    })(mygps.service.AbstractService)
  });

  namespace('mygps.service', {
    SessionService: SessionService = (function(_super) {

      __extends(SessionService, _super);

      function SessionService(baseURL) {
        SessionService.__super__.constructor.call(this, baseURL);
      }

      SessionService.prototype.getAuthenticatedPerson = function(callbacks) {
        return $.ajax({
          url: this.createURL("/getAuthenticatedPerson"),
          dataType: "json",
          success: function(result) {
            var person;
            if (result) {
              person = mygps.model.Person.createFromTransferObject(result);
            } else {
              person = null;
            }
            return callbacks != null ? typeof callbacks.result === "function" ? callbacks.result(person) : void 0 : void 0;
          },
          error: function(fault) {
            return callbacks != null ? typeof callbacks.fault === "function" ? callbacks.fault(fault) : void 0 : void 0;
          }
        });
      };

      return SessionService;

    })(mygps.service.AbstractService)
  });

  namespace('mygps.service', {
    StudentIntakeService: StudentIntakeService = (function(_super) {

      __extends(StudentIntakeService, _super);

      function StudentIntakeService(baseURL) {
        StudentIntakeService.__super__.constructor.call(this, baseURL);
      }

      StudentIntakeService.prototype.getForm = function(callbacks) {
        return $.ajax({
          url: this.createURL("/getForm"),
          dataType: "json",
          success: function(result) {
            var form;
            form = mygps.model.Form.createFromTransferObject(result);
            return callbacks != null ? typeof callbacks.result === "function" ? callbacks.result(form) : void 0 : void 0;
          },
          error: function(fault) {
            return callbacks != null ? typeof callbacks.fault === "function" ? callbacks.fault(fault) : void 0 : void 0;
          }
        });
      };

      StudentIntakeService.prototype.saveForm = function(form, callbacks) {
        return $.ajax({
          url: this.createURL(""),
          type: "POST",
          data: JSON.stringify(mygps.model.Form.toTransferObject(form)),
          contentType: "application/json",
          success: function(result) {
            return callbacks != null ? typeof callbacks.result === "function" ? callbacks.result(result) : void 0 : void 0;
          },
          error: function(fault) {
            return callbacks != null ? typeof callbacks.fault === "function" ? callbacks.fault(fault) : void 0 : void 0;
          }
        });
      };

      return StudentIntakeService;

    })(mygps.service.AbstractService)
  });

  namespace('mygps.service', {
    TaskService: TaskService = (function(_super) {

      __extends(TaskService, _super);

      function TaskService(baseURL) {
        TaskService.__super__.constructor.call(this, baseURL);
      }

      TaskService.prototype.createCustom = function(name, description, callbacks) {
        return $.ajax({
          url: this.createURL("/createCustom?name=" + (encodeURIComponent(name)) + "&description=" + (encodeURIComponent(description))),
          dataType: "json",
          success: function(result) {
            var task;
            task = mygps.model.Task.createFromTransferObject(result);
            return callbacks != null ? typeof callbacks.result === "function" ? callbacks.result(task) : void 0 : void 0;
          },
          error: function(fault) {
            return callbacks != null ? typeof callbacks.fault === "function" ? callbacks.fault(fault) : void 0 : void 0;
          }
        });
      };

      TaskService.prototype.createForChallengeReferral = function(challengeId, challengeReferralId, callbacks) {
        return $.ajax({
          url: this.createURL("/createForChallengeReferral?challengeId=" + challengeId + "&challengeReferralId=" + challengeReferralId),
          dataType: "json",
          success: function(result) {
            var task;
            task = mygps.model.Task.createFromTransferObject(result);
            return callbacks != null ? typeof callbacks.result === "function" ? callbacks.result(task) : void 0 : void 0;
          },
          error: function(fault) {
            return callbacks != null ? typeof callbacks.fault === "function" ? callbacks.fault(fault) : void 0 : void 0;
          }
        });
      };

      TaskService.prototype["delete"] = function(taskId, callbacks) {
        return $.ajax({
          url: this.createURL("/delete?taskId=" + taskId),
          dataType: "json",
          success: function(result) {
            return callbacks != null ? typeof callbacks.result === "function" ? callbacks.result(result) : void 0 : void 0;
          },
          error: function(fault) {
            return callbacks != null ? typeof callbacks.fault === "function" ? callbacks.fault(fault) : void 0 : void 0;
          }
        });
      };

      TaskService.prototype.email = function(emailAddress, callbacks) {
        return $.ajax({
          url: this.createURL("/email?emailAddress=" + emailAddress),
          dataType: "json",
          success: function(result) {
            return callbacks != null ? typeof callbacks.result === "function" ? callbacks.result(result) : void 0 : void 0;
          },
          error: function(fault) {
            return callbacks != null ? typeof callbacks.fault === "function" ? callbacks.fault(fault) : void 0 : void 0;
          }
        });
      };

      TaskService.prototype.getAll = function(callbacks) {
        return $.ajax({
          url: this.createURL("/getAll"),
          dataType: "json",
          success: function(result) {
            var task, tasks;
            tasks = (function() {
              var _i, _len, _results;
              _results = [];
              for (_i = 0, _len = result.length; _i < _len; _i++) {
                task = result[_i];
                _results.push(mygps.model.Task.createFromTransferObject(task));
              }
              return _results;
            })();
            return callbacks != null ? typeof callbacks.result === "function" ? callbacks.result(tasks) : void 0 : void 0;
          },
          error: function(fault) {
            return callbacks != null ? typeof callbacks.fault === "function" ? callbacks.fault(fault) : void 0 : void 0;
          }
        });
      };

      TaskService.prototype.mark = function(taskId, complete, callbacks) {
        return $.ajax({
          url: this.createURL("/mark?taskId=" + taskId + "&complete=" + complete),
          dataType: "json",
          success: function(result) {
            var task;
            task = mygps.model.Task.createFromTransferObject(result);
            return callbacks != null ? typeof callbacks.result === "function" ? callbacks.result(task) : void 0 : void 0;
          },
          error: function(fault) {
            return callbacks != null ? typeof callbacks.fault === "function" ? callbacks.fault(fault) : void 0 : void 0;
          }
        });
      };

      TaskService.prototype.print = function(callbacks) {
        window.open(this.createURL("/print"));
        return result(true);
        /*
        				$.ajax(
        					url: @createURL( "/print" )
        					dataType: "json"
        					success: ( result ) ->
        						callbacks?.result?( result )
        					error: ( fault ) ->
        						callbacks?.fault?( fault )
        				)
        */
      };

      return TaskService;

    })(mygps.service.AbstractService)
  });

  namespace('mygps.viewmodel', {
    AbstractSessionViewModel: AbstractSessionViewModel = (function() {

      function AbstractSessionViewModel(session) {
        this.session = session;
        this.authenticated = ko.dependentObservable(this.evaluateAuthenticated, this);
        this.authenticatedPersonName = ko.dependentObservable(this.authenticatedPersonName, this);
      }

      AbstractSessionViewModel.prototype.load = function() {};

      AbstractSessionViewModel.prototype.evaluateAuthenticated = function() {
        var _ref;
        return ((_ref = this.session) != null ? _ref.authenticatedPerson() : void 0) != null;
      };

      AbstractSessionViewModel.prototype.authenticatedPersonName = function() {
        var person, _ref;
        person = (_ref = this.session) != null ? _ref.authenticatedPerson() : void 0;
        if (person != null) {
          return "" + (person.firstName()) + " " + (person.lastName());
        }
        return null;
      };

      return AbstractSessionViewModel;

    })()
  });

  namespace('mygps.viewmodel', {
    AbstractTasksViewModel: AbstractTasksViewModel = (function(_super) {

      __extends(AbstractTasksViewModel, _super);

      function AbstractTasksViewModel(session, taskService) {
        AbstractTasksViewModel.__super__.constructor.call(this, session);
        this.taskService = taskService;
        this.tasks = ko.observableArray([]);
        this.taskFilters = ko.observableArray(mygps.enumeration.TaskFilter.enumerators);
        this.selectedTaskFilter = ko.observable(mygps.enumeration.TaskFilter.ALL);
        this.filteredTasks = ko.dependentObservable(this.filterTasks, this);
        this.printingTasks = ko.observable(false);
        this.emailingTasks = ko.observable(false);
      }

      AbstractTasksViewModel.prototype.load = function() {
        AbstractTasksViewModel.__super__.load.call(this);
        this.loadAllTasks();
      };

      AbstractTasksViewModel.prototype.formatDate = function(value) {
        var now;
        if (value != null) {
          now = new Date();
          if (value.getFullYear() === now.getFullYear()) {
            return "" + (value.getMonth() + 1) + "/" + (value.getDate());
          } else {
            return "" + (value.getMonth() + 1) + "/" + (value.getDate()) + "/" + (("" + value.getFullYear()).substring(2));
          }
        }
        return "";
      };

      AbstractTasksViewModel.prototype.filterTasks = function() {
        return _.select(this.tasks(), this.selectedTaskFilter().filterFunction);
      };

      AbstractTasksViewModel.prototype.loadAllTasks = function() {
        var _this = this;
        this.taskService.getAll({
          result: function(result) {
            return _this.tasks(result);
          },
          fault: function(fault) {
            return alert(fault.responseText);
          }
        });
      };

      AbstractTasksViewModel.prototype.selectTaskFilter = function(taskFilter) {
        this.selectedTaskFilter(taskFilter);
      };

      AbstractTasksViewModel.prototype.createCustomTask = function(name, description, callbacks) {
        var _this = this;
        this.taskService.createCustom(name, description, {
          result: function(result) {
            _this.tasks.push(result);
            return callbacks != null ? typeof callbacks.result === "function" ? callbacks.result(result) : void 0 : void 0;
          },
          fault: function(fault) {
            alert(fault.responseText);
            return callbacks != null ? typeof callbacks.fault === "function" ? callbacks.fault(fault) : void 0 : void 0;
          }
        });
      };

      AbstractTasksViewModel.prototype.createTaskForChallengeReferral = function(challenge, challengeReferral) {
        var _this = this;
        this.taskService.createForChallengeReferral(challenge.id(), challengeReferral.id(), {
          result: function(result) {
            return _this.tasks.push(result);
          },
          fault: function(fault) {
            return alert(fault.responseText);
          }
        });
      };

      AbstractTasksViewModel.prototype.createTaskForChallengeReferral = function(challenge, challengeReferral, callbacks) {
        var _this = this;
        this.taskService.createForChallengeReferral(challenge.id(), challengeReferral.id(), {
          result: function(result) {
            _this.tasks.push(result);
            return callbacks != null ? typeof callbacks.result === "function" ? callbacks.result(result) : void 0 : void 0;
          },
          fault: function(fault) {
            alert(fault.responseText);
            return callbacks != null ? typeof callbacks.fault === "function" ? callbacks.fault(fault) : void 0 : void 0;
          }
        });
      };

      AbstractTasksViewModel.prototype.markTask = function(task, complete) {
        var _this = this;
        this.taskService.mark(task.id(), complete, {
          result: function(result) {
            return task.completed(complete);
          },
          fault: function(fault) {
            return alert(fault.responseText);
          }
        });
      };

      AbstractTasksViewModel.prototype.deleteTask = function(task) {
        var _this = this;
        this.taskService["delete"](task.id(), {
          result: function(result) {
            return _this.tasks.remove(task);
          },
          fault: function(fault) {
            return alert(fault.responseText);
          }
        });
      };

      AbstractTasksViewModel.prototype.deleteTask = function(task, callbacks) {
        var _this = this;
        this.taskService["delete"](task.id(), {
          result: function(result) {
            _this.tasks.remove(task);
            return callbacks != null ? typeof callbacks.result === "function" ? callbacks.result(result) : void 0 : void 0;
          },
          fault: function(fault) {
            alert(fault.responseText);
            return callbacks != null ? typeof callbacks.fault === "function" ? callbacks.fault(fault) : void 0 : void 0;
          }
        });
      };

      AbstractTasksViewModel.prototype.printTasks = function() {
        var _this = this;
        this.printingTasks(true);
        this.taskService.print({
          result: function(result) {
            return _this.printingTasks(false);
          },
          fault: function(fault) {
            alert(fault.responseText);
            return _this.printingTasks(false);
          }
        });
      };

      AbstractTasksViewModel.prototype.emailTasks = function(emailAddress) {
        var _this = this;
        this.emailingTasks(true);
        this.taskService.email(emailAddress, {
          result: function(result) {
            return _this.emailingTasks(false);
          },
          fault: function(result) {
            alert(fault.responseText);
            return _this.emailingTasks(false);
          }
        });
      };

      AbstractTasksViewModel.prototype.collectEmailAddress = function() {
        var _this = this;
        apprise('E-mail address:', {
          'input': true,
          'validation': function(value) {
            if (/^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,4}$/i.test(value)) {
              return 'true';
            } else {
              return 'invalid email address';
            }
          }
        }, function(result) {
          if (result) return _this.emailTasks(result);
        });
      };

      return AbstractTasksViewModel;

    })(mygps.viewmodel.AbstractSessionViewModel)
  });

  namespace('mygps.viewmodel', {
    ChallengeReferralSearchViewModel: ChallengeReferralSearchViewModel = (function(_super) {

      __extends(ChallengeReferralSearchViewModel, _super);

      function ChallengeReferralSearchViewModel(session, taskService, challengeService, challengeReferralService) {
        ChallengeReferralSearchViewModel.__super__.constructor.call(this, session, taskService);
        this.challengeService = challengeService;
        this.challengeReferralService = challengeReferralService;
        this.query = ko.observable(null);
        this.challenges = ko.observableArray();
        this.selectedChallenge = ko.observable(null);
        this.selectedChallengeName = ko.dependentObservable(this.evaluateSelectedChallengeName, this);
        this.referrals = ko.dependentObservable(this.evaluateReferrals, this);
        this.filteredReferrals = ko.dependentObservable(this.filterReferrals, this);
        this.allowCustomTaskCreation = ko.observable(false);
      }

      ChallengeReferralSearchViewModel.prototype.evaluateReferrals = function() {
        var _ref;
        return ((_ref = this.selectedChallenge()) != null ? _ref.challengeReferrals() : void 0) || [];
      };

      ChallengeReferralSearchViewModel.prototype.evaluateSelectedChallengeName = function() {
        var _ref;
        return (_ref = this.selectedChallenge()) != null ? _ref.name() : void 0;
      };

      ChallengeReferralSearchViewModel.prototype.filterReferrals = function() {
        var _this = this;
        return _.select(this.referrals(), function(referral) {
          return !_.any(_this.tasks(), function(task) {
            return task.challengeReferralId() === referral.id() && !task.completed();
          });
        });
      };

      ChallengeReferralSearchViewModel.prototype.load = function(query) {
        if (query == null) query = "";
        ChallengeReferralSearchViewModel.__super__.load.call(this);
        this.search(query);
      };

      ChallengeReferralSearchViewModel.prototype.createTaskForChallengeReferral = function(challenge, challengeReferral) {
        var _this = this;
        ChallengeReferralSearchViewModel.__super__.createTaskForChallengeReferral.call(this, challenge, challengeReferral, {
          result: function(result) {
            return _this.refresh();
          },
          fault: function(fault) {
            return alert(fault.responseText);
          }
        });
      };

      ChallengeReferralSearchViewModel.prototype.markTask = function(task, complete) {
        ChallengeReferralSearchViewModel.__super__.markTask.call(this, task, complete);
        this.refresh();
      };

      ChallengeReferralSearchViewModel.prototype.deleteTask = function(task) {
        var _this = this;
        ChallengeReferralSearchViewModel.__super__.deleteTask.call(this, task, {
          result: function(result) {
            return _this.refresh();
          },
          fault: function(fault) {
            return alert(fault.responseText);
          }
        });
      };

      ChallengeReferralSearchViewModel.prototype.search = function(query) {
        this.query(query);
        this.reset();
        if ((query != null)) this.searchChallenges(query);
      };

      ChallengeReferralSearchViewModel.prototype.refresh = function() {
        return this.searchChallenges(this.query());
      };

      ChallengeReferralSearchViewModel.prototype.reset = function() {
        this.challenges.removeAll();
        return this.selectedChallenge(null);
      };

      ChallengeReferralSearchViewModel.prototype.selectChallenge = function(challenge) {
        this.selectedChallenge(challenge);
        if ((this.selectedChallenge() != null)) {
          if (this.selectedChallenge().referralCount() > 0 && this.selectedChallenge().challengeReferrals().length === 0) {
            return this.searchChallengeReferrals(this.selectedChallenge(), this.query());
          }
        }
      };

      ChallengeReferralSearchViewModel.prototype.searchChallenges = function(query) {
        var _this = this;
        this.challengeService.search(query, {
          result: function(result) {
            _this.challenges(result);
            return _this.allowCustomTaskCreation(_this.challenges().length === 0);
          },
          fault: function(fault) {
            return alert(fault.responseText);
          }
        });
      };

      ChallengeReferralSearchViewModel.prototype.searchChallengeReferrals = function(challenge, query) {
        var _this = this;
        return this.challengeReferralService.search(challenge.id(), query, {
          result: function(result) {
            return challenge.challengeReferrals(result);
          },
          fault: function(fault) {
            return alert(fault.responseText);
          }
        });
      };

      return ChallengeReferralSearchViewModel;

    })(mygps.viewmodel.AbstractTasksViewModel)
  });

  namespace('mygps.viewmodel', {
    ContactViewModel: ContactViewModel = (function(_super) {

      __extends(ContactViewModel, _super);

      function ContactViewModel(session, messageService) {
        ContactViewModel.__super__.constructor.call(this, session);
        this.messageService = messageService;
        this.subject = ko.observable(null);
        this.message = ko.observable(null);
        this.contactingCoach = ko.observable(false);
      }

      ContactViewModel.prototype.load = function() {
        ContactViewModel.__super__.load.call(this);
      };

      ContactViewModel.prototype.contactCoach = function(subject, message, callbacks) {
        var _this = this;
        this.contactingCoach(true);
        this.messageService.contactCoach(subject, message, {
          result: function(result) {
            _this.contactingCoach(false);
            return callbacks != null ? typeof callbacks.result === "function" ? callbacks.result(result) : void 0 : void 0;
          },
          fault: function(fault) {
            if (fault.responseText.indexOf("org.hibernate.exception.ConstraintViolationException") !== -1) {
              alert("You must provide a Subject and a Message before it can be sent to your coach.");
            } else {
              alert(fault.responseText);
            }
            _this.contactingCoach(false);
            return callbacks != null ? typeof callbacks.fault === "function" ? callbacks.fault(fault) : void 0 : void 0;
          }
        });
      };

      return ContactViewModel;

    })(mygps.viewmodel.AbstractSessionViewModel)
  });

  namespace('mygps.viewmodel', {
    HomeViewModel: HomeViewModel = (function(_super) {

      __extends(HomeViewModel, _super);

      function HomeViewModel(session, taskService) {
        HomeViewModel.__super__.constructor.call(this, session, taskService);
        this.canContactCoach = ko.dependentObservable(this.evaluateCanContactCoach, this);
      }

      HomeViewModel.prototype.load = function() {
        HomeViewModel.__super__.load.call(this);
      };

      HomeViewModel.prototype.evaluateCanContactCoach = function() {
        var _ref, _ref2;
        return ((_ref = this.session) != null ? (_ref2 = _ref.authenticatedPerson()) != null ? _ref2.coach() : void 0 : void 0) != null;
      };

      return HomeViewModel;

    })(mygps.viewmodel.AbstractTasksViewModel)
  });

  namespace('mygps.viewmodel', {
    LoginViewModel: LoginViewModel = (function() {

      function LoginViewModel() {}

      LoginViewModel.prototype.load = function() {};

      return LoginViewModel;

    })()
  });

  namespace('mygps.viewmodel', {
    SelfHelpGuidesViewModel: SelfHelpGuidesViewModel = (function(_super) {

      __extends(SelfHelpGuidesViewModel, _super);

      function SelfHelpGuidesViewModel(session, selfHelpGuideService) {
        SelfHelpGuidesViewModel.__super__.constructor.call(this, session);
        this.selfHelpGuideService = selfHelpGuideService;
        this.selfHelpGuides = ko.observableArray();
      }

      SelfHelpGuidesViewModel.prototype.load = function(selfHelpGuideGroupId) {
        if (selfHelpGuideGroupId == null) selfHelpGuideGroupId = null;
        SelfHelpGuidesViewModel.__super__.load.call(this);
        this.loadSelfHelpGuides(selfHelpGuideGroupId);
      };

      SelfHelpGuidesViewModel.prototype.loadSelfHelpGuides = function(selfHelpGuideGroupId) {
        var _this = this;
        if (selfHelpGuideGroupId == null) selfHelpGuideGroupId = null;
        if (selfHelpGuideGroupId != null) {
          this.selfHelpGuideService.getBySelfHelpGuideGroup(selfHelpGuideGroupId, {
            result: function(result) {
              return _this.selfHelpGuides(result);
            },
            fault: function(fault) {
              return alert(fault.responseText);
            }
          });
        } else {
          this.selfHelpGuideService.getAll({
            result: function(result) {
              return _this.selfHelpGuides(result);
            },
            fault: function(fault) {
              return alert(fault.responseText);
            }
          });
        }
      };

      return SelfHelpGuidesViewModel;

    })(mygps.viewmodel.AbstractSessionViewModel)
  });

  namespace('mygps.viewmodel', {
    SelfHelpGuideViewModel: SelfHelpGuideViewModel = (function(_super) {

      __extends(SelfHelpGuideViewModel, _super);

      function SelfHelpGuideViewModel(session, taskService, selfHelpGuideService, selfHelpGuideResponseService, challengeReferralService) {
        SelfHelpGuideViewModel.__super__.constructor.call(this, session, taskService);
        this.selfHelpGuideService = selfHelpGuideService;
        this.selfHelpGuideResponseService = selfHelpGuideResponseService;
        this.challengeReferralService = challengeReferralService;
        this.selfHelpGuideContent = ko.observable(null);
        this.selfHelpGuideResponseId = ko.observable(null);
        this.selfHelpGuideResponse = ko.observable(null);
        this.name = ko.dependentObservable(this.evaluateName, this);
        this.introductoryText = ko.dependentObservable(this.evaluateIntroductoryText, this);
        this.currentQuestionIndex = ko.observable(0);
        this.currentQuestion = ko.dependentObservable(this.evaluateCurrentQuestion, this);
        this.hasPreviousQuestion = ko.dependentObservable(this.evaluateHasPreviousQuestion, this);
        this.hasNextQuestion = ko.dependentObservable(this.evaluateHasNextQuestion, this);
        this.canSkipQuestion = ko.dependentObservable(this.evaluateCanSkipQuestion, this);
        this.progressText = ko.dependentObservable(this.evaluateProgressText, this);
        this.challenges = ko.dependentObservable(this.evaluateChallenges, this);
        this.selectedChallenge = ko.observable(null);
        this.selectedChallengeName = ko.dependentObservable(this.evaluateSelectedChallengeName, this);
        this.referrals = ko.dependentObservable(this.evaluateReferrals, this);
        this.filteredReferrals = ko.dependentObservable(this.filterReferrals, this);
        this.summaryText = ko.dependentObservable(this.evaluateSummaryText, this);
      }

      SelfHelpGuideViewModel.prototype.load = function(selfHelpGuideId) {
        SelfHelpGuideViewModel.__super__.load.call(this);
        this.initiateSelfHelpGuideResponse(selfHelpGuideId);
      };

      SelfHelpGuideViewModel.prototype.createTaskForChallengeReferral = function(challenge, challengeReferral) {
        var _this = this;
        SelfHelpGuideViewModel.__super__.createTaskForChallengeReferral.call(this, challenge, challengeReferral, {
          result: function(result) {
            return _this.refresh();
          },
          fault: function(fault) {
            return alert(fault.responseText);
          }
        });
      };

      SelfHelpGuideViewModel.prototype.markTask = function(task, complete) {
        SelfHelpGuideViewModel.__super__.markTask.call(this, task, complete);
        this.refresh();
      };

      SelfHelpGuideViewModel.prototype.deleteTask = function(task) {
        SelfHelpGuideViewModel.__super__.deleteTask.call(this, task);
        this.refresh();
      };

      SelfHelpGuideViewModel.prototype.evaluateName = function() {
        var _ref;
        return (_ref = this.selfHelpGuideContent()) != null ? _ref.name() : void 0;
      };

      SelfHelpGuideViewModel.prototype.evaluateIntroductoryText = function() {
        var _ref;
        return (_ref = this.selfHelpGuideContent()) != null ? _ref.introductoryText() : void 0;
      };

      SelfHelpGuideViewModel.prototype.evaluateCurrentQuestion = function() {
        var _ref, _ref2;
        return (_ref = this.selfHelpGuideContent()) != null ? (_ref2 = _ref.questions()) != null ? _ref2[this.currentQuestionIndex()] : void 0 : void 0;
      };

      SelfHelpGuideViewModel.prototype.evaluateHasPreviousQuestion = function() {
        if (this.selfHelpGuideContent() != null) {
          return this.currentQuestionIndex() > 0;
        }
        return false;
      };

      SelfHelpGuideViewModel.prototype.evaluateHasNextQuestion = function() {
        if (this.selfHelpGuideContent() != null) {
          return this.currentQuestionIndex() < this.selfHelpGuideContent().questions().length - 1;
        }
        return false;
      };

      SelfHelpGuideViewModel.prototype.evaluateCanSkipQuestion = function() {
        if (this.currentQuestion() != null) {
          return !this.currentQuestion().mandatory();
        }
        return true;
      };

      SelfHelpGuideViewModel.prototype.evaluateProgressText = function() {
        if (this.selfHelpGuideContent() != null) {
          return "" + (this.currentQuestionIndex() + 1) + " of " + (this.selfHelpGuideContent().questions().length);
        } else {
          return "";
        }
      };

      SelfHelpGuideViewModel.prototype.evaluateChallenges = function() {
        var _ref;
        return ((_ref = this.selfHelpGuideResponse()) != null ? _ref.challengesIdentified() : void 0) || [];
      };

      SelfHelpGuideViewModel.prototype.evaluateSelectedChallengeName = function() {
        var _ref;
        return (_ref = this.selectedChallenge()) != null ? _ref.name() : void 0;
      };

      SelfHelpGuideViewModel.prototype.evaluateReferrals = function() {
        var _ref;
        return ((_ref = this.selectedChallenge()) != null ? _ref.challengeReferrals() : void 0) || [];
      };

      SelfHelpGuideViewModel.prototype.evaluateSummaryText = function() {
        var _ref;
        return (_ref = this.selfHelpGuideResponse()) != null ? _ref.summaryText() : void 0;
      };

      SelfHelpGuideViewModel.prototype.filterReferrals = function() {
        var _this = this;
        return _.select(this.referrals(), function(referral) {
          return !_.any(_this.tasks(), function(task) {
            return task.challengeReferralId() === referral.id() && !task.completed();
          });
        });
      };

      SelfHelpGuideViewModel.prototype.loadSelfHelpGuideContent = function(selfHelpGuideId) {
        var _this = this;
        this.selfHelpGuideService.getContentById(selfHelpGuideId, {
          result: function(result) {
            return _this.selfHelpGuideContent(result);
          },
          fault: function(fault) {
            return alert(fault.responseText);
          }
        });
      };

      SelfHelpGuideViewModel.prototype.initiateSelfHelpGuideResponse = function(selfHelpGuideId) {
        var _this = this;
        this.selfHelpGuideResponseService.initiate(selfHelpGuideId, {
          result: function(result) {
            _this.selfHelpGuideResponseId(result);
            return _this.loadSelfHelpGuideContent(selfHelpGuideId);
          },
          fault: function(fault) {
            return alert(fault.responseText);
          }
        });
      };

      SelfHelpGuideViewModel.prototype.answerQuestion = function(selfHelpGuideQuestion, response) {
        var _this = this;
        this.selfHelpGuideResponseService.answer(this.selfHelpGuideResponseId(), selfHelpGuideQuestion.id(), response, {
          result: function(result) {
            selfHelpGuideQuestion.response(response);
            return _this.moveToNextQuestion();
          },
          fault: function(fault) {
            return alert(fault.responseText);
          }
        });
      };

      SelfHelpGuideViewModel.prototype.skipToNextQuestion = function() {
        if (!this.currentQuestion().mandatory()) return this.moveToNextQuestion();
      };

      SelfHelpGuideViewModel.prototype.moveToPreviousQuestion = function() {
        if (this.hasPreviousQuestion()) {
          this.currentQuestionIndex(this.currentQuestionIndex() - 1);
        }
      };

      SelfHelpGuideViewModel.prototype.moveToNextQuestion = function() {
        if (this.hasNextQuestion()) {
          this.currentQuestionIndex(this.currentQuestionIndex() + 1);
        } else {
          this.complete();
        }
      };

      SelfHelpGuideViewModel.prototype.cancel = function() {
        var _this = this;
        this.selfHelpGuideResponseService.cancel(this.selfHelpGuideResponse().id(), {
          result: function(result) {
            return _this.selfHelpGuideResponse().cancelled(true);
          },
          fault: function(fault) {
            return alert(fault.responseText);
          }
        });
      };

      SelfHelpGuideViewModel.prototype.complete = function() {
        var _this = this;
        this.selfHelpGuideResponseService.complete(this.selfHelpGuideResponseId(), {
          result: function(result) {
            return _this.selfHelpGuideResponseService.getById(_this.selfHelpGuideResponseId(), {
              result: function(result) {
                return _this.selfHelpGuideResponse(result);
              },
              fault: function(fault) {
                return alert(fault.responseText);
              }
            });
          },
          fault: function(fault) {
            return alert(fault.responseText);
          }
        });
      };

      SelfHelpGuideViewModel.prototype.refresh = function() {
        var _this = this;
        return this.selfHelpGuideResponseService.getById(this.selfHelpGuideResponseId(), {
          result: function(result) {
            return _this.selfHelpGuideResponse(result);
          },
          fault: function(fault) {
            return alert(fault.responseText);
          }
        });
      };

      SelfHelpGuideViewModel.prototype.selectChallenge = function(challenge) {
        var _this = this;
        this.selectedChallenge(challenge);
        this.challengeReferralService.getByChallengeId(challenge.id(), {
          result: function(result) {
            return challenge.challengeReferrals(result);
          },
          fault: function(fault) {
            return alert(fault.responseText);
          }
        });
      };

      return SelfHelpGuideViewModel;

    })(mygps.viewmodel.AbstractTasksViewModel)
  });

  namespace('mygps.viewmodel', {
    StudentIntakeViewModel: StudentIntakeViewModel = (function(_super) {

      __extends(StudentIntakeViewModel, _super);

      function StudentIntakeViewModel(session, studentIntakeService) {
        StudentIntakeViewModel.__super__.constructor.call(this, session);
        this.studentIntakeService = studentIntakeService;
        this.form = ko.observable(null);
        this.savingForm = ko.observable(false);
        this.invalid = ko.observable(false);
        this.currentSectionIndex = ko.observable(0);
        this.currentSection = ko.dependentObservable(this.evaluateCurrentSection, this);
        this.hasPreviousSection = ko.dependentObservable(this.evaluateHasPreviousSection, this);
        this.hasNextSection = ko.dependentObservable(this.evaluateHasNextSection, this);
      }

      StudentIntakeViewModel.prototype.load = function() {
        StudentIntakeViewModel.__super__.load.call(this);
        this.loadForm();
      };

      StudentIntakeViewModel.prototype.evaluateCurrentSection = function() {
        var _ref, _ref2;
        return (_ref = this.form()) != null ? (_ref2 = _ref.sections()) != null ? _ref2[this.currentSectionIndex()] : void 0 : void 0;
      };

      StudentIntakeViewModel.prototype.evaluateHasPreviousSection = function() {
        if (this.form() != null) return this.currentSectionIndex() > 0;
        return false;
      };

      StudentIntakeViewModel.prototype.evaluateHasNextSection = function() {
        if (this.form() != null) {
          return this.currentSectionIndex() < this.form().sections().length - 1;
        }
        return false;
      };

      StudentIntakeViewModel.prototype.loadForm = function() {
        var _this = this;
        this.studentIntakeService.getForm({
          result: function(result) {
            return _this.form(result);
          },
          fault: function(fault) {
            return alert(fault.responseText);
          }
        });
      };

      StudentIntakeViewModel.prototype.saveForm = function(callbacks) {
        var _this = this;
        this.currentSection().validate();
        if (this.currentSection().valid()) {
          this.invalid(false);
          this.savingForm(true);
          this.studentIntakeService.saveForm(this.form(), {
            result: function(result) {
              _this.savingForm(false);
              return callbacks != null ? typeof callbacks.result === "function" ? callbacks.result(result) : void 0 : void 0;
            },
            fault: function(fault) {
              _this.savingForm(false);
              alert(fault.responseText);
              return callbacks != null ? typeof callbacks.fault === "function" ? callbacks.fault(fault) : void 0 : void 0;
            }
          });
        } else {
          this.invalid(true);
        }
      };

      StudentIntakeViewModel.prototype.moveToPreviousSection = function() {
        if (this.hasPreviousSection()) {
          this.currentSectionIndex(this.currentSectionIndex() - 1);
          this.invalid(false);
          return true;
        }
        return false;
      };

      StudentIntakeViewModel.prototype.moveToNextSection = function() {
        if (this.hasNextSection()) {
          this.currentSection().validate();
          if (this.currentSection().valid()) {
            this.invalid(false);
            this.currentSectionIndex(this.currentSectionIndex() + 1);
            return true;
          } else {
            this.invalid(true);
          }
        }
        return false;
      };

      return StudentIntakeViewModel;

    })(mygps.viewmodel.AbstractSessionViewModel)
  });

}).call(this);