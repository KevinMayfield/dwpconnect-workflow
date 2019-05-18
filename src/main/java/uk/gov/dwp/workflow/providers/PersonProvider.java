package uk.gov.dwp.workflow.providers;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.ValidationModeEnum;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.OperationOutcome;
import org.hl7.fhir.dstu3.model.Person;
import org.hl7.fhir.dstu3.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.dwp.workflow.dao.PersonRepository;
import uk.gov.dwp.workflow.support.OperationOutcomeFactory;
import uk.gov.dwp.workflow.support.ProviderResponseLibrary;


import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

//import uk.nhs.careconnect.ri.lib.gateway.provider.ResourceTestProvider;

@Component
public class PersonProvider implements ICCResourceProvider {
	
   
    @Autowired
    private PersonRepository personDao;

    @Autowired
    FhirContext ctx;

    @Override
    public Long count() {
        return personDao.count();
    }

    @Override
    public Class<Person> getResourceType() {
        return Person.class;
    }

    private static final Logger log = LoggerFactory.getLogger(PersonProvider.class);


    @Read
    public Person read(@IdParam IdType internalId) {

    	
        Person person = personDao.read(ctx,internalId);
        if (person == null) {
            throw OperationOutcomeFactory.buildOperationOutcomeException(
                    new ResourceNotFoundException("No person details found for person ID: " + internalId.getIdPart()),
                    OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTFOUND);
        }

        return person;
    }


    @Update
    public MethodOutcome update(HttpServletRequest theRequest, @ResourceParam Person person, @IdParam IdType theId, @ConditionalUrlParam String theConditional, RequestDetails theRequestDetails) {

        log.debug("Update Person Provider called");
        

        MethodOutcome method = new MethodOutcome();
        method.setCreated(true);
        OperationOutcome opOutcome = new OperationOutcome();

        method.setOperationOutcome(opOutcome);
        Person newPerson = null;
        try {
            newPerson = personDao.update(ctx, person, theId, theConditional);
        } catch (Exception ex) {
            ProviderResponseLibrary.handleException(method,ex);
        }
        method.setId(newPerson.getIdElement());
        method.setResource(newPerson);


        log.debug("called update Person method");

        return method;
    }

    @Create
    public MethodOutcome createPerson(HttpServletRequest theRequest, @ResourceParam Person person) {

        log.info("Update Person Provider called");

        MethodOutcome method = new MethodOutcome();
        method.setCreated(true);
        OperationOutcome opOutcome = new OperationOutcome();

        method.setOperationOutcome(opOutcome);
        Person newPerson = null;
        try {
            newPerson = personDao.update(ctx, person, null, null);
            method.setId(newPerson.getIdElement());
            method.setResource(newPerson);
        } catch(Exception ex) {
            ProviderResponseLibrary.handleException(method,ex);
        }

        log.debug("called create Person method");

        return method;
    }

    @Search
    public List<Resource> searchPerson(HttpServletRequest theRequest,
        @OptionalParam(name= Person.SP_NAME) StringParam name
     ) {
        return personDao.search(ctx,name);
    }



}