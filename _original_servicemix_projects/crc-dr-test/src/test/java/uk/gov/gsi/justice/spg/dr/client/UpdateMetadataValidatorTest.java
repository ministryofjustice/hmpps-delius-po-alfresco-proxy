package uk.gov.gsi.justice.spg.dr.client;

import org.junit.Test;
import uk.gov.gsi.justice.spg.dr.client.validators.UpdateMetadataValidator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UpdateMetadataValidatorTest {

    @Test
    public void validMeatadaParametersTest(){
        String [] args = {"updatemetadata" ,"123",};
        assertFalse(UpdateMetadataValidator.isValidUpdateMetadataCommand(args));
    }

    @Test
    public void validDocTypeParametersTest(){
        String [] args = {"updatemetadata" , "123", "docType=sdfsdf"};
        String [] args1 = {"updatemetadata" , "123", "docType=12"};
        String [] args2 = {"updatemetadata" , "123", "docType=DOCUMENT"};
        String [] args3 = {"updatemetadata" , "123", "docType=PREVIOUS_CONVICTION"};
        assertFalse(UpdateMetadataValidator.isValidUpdateMetadataCommand(args));
        assertFalse(UpdateMetadataValidator.isValidUpdateMetadataCommand(args1));
        assertTrue(UpdateMetadataValidator.isValidUpdateMetadataCommand(args2));
        assertTrue(UpdateMetadataValidator.isValidUpdateMetadataCommand(args3));
    }

    @Test
    public void validEntityIdParametersTest(){
        String [] args = {"updatemetadata" , "123", "entityId=sdfsdf"};
        String [] args1 = {"updatemetadata" , "123", "entityId=12"};
        assertFalse(UpdateMetadataValidator.isValidUpdateMetadataCommand(args));
        assertTrue(UpdateMetadataValidator.isValidUpdateMetadataCommand(args1));
    }

    @Test
    public void validEntityTypeTest(){
        String [] args = {"updatemetadata" , "123", "entityType=sdfsdf"};
        String [] args1 = {"updatemetadata" , "123", "entityType=APREFERRAL"};
        String [] args2 = {"updatemetadata" , "123", "entityType=ADDRESSASSESSMENT"};
        String [] args3 = {"updatemetadata" , "123", "entityType=RATECARDINTERVENTION"};
        assertFalse(UpdateMetadataValidator.isValidUpdateMetadataCommand(args));
        assertTrue(UpdateMetadataValidator.isValidUpdateMetadataCommand(args1));
        assertTrue(UpdateMetadataValidator.isValidUpdateMetadataCommand(args2));
        assertTrue(UpdateMetadataValidator.isValidUpdateMetadataCommand(args3));
    }


}
