package uk.gov.gsi.justice.spg.dr.client;

import org.junit.Test;
import uk.gov.gsi.justice.spg.dr.client.validators.SearchCommandValidator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class SearchCommandValidatorTest {

    @Test
    public void validSearchNumberParameters() {
        String[] args = {"search", "X030927", "maxResults=10"};
        assertTrue(SearchCommandValidator.isValidSearchCommand(args));
    }

    @Test
    public void validMaxResultsParameters() {
        String[] args = {"search", "X030927", "maxResults=sdfsdf"};
        String[] args1 = {"search", "X030927", "maxResults=12"};
        String[] args2 = {"search", "X030927", "maxResults=-12"};
        assertFalse(SearchCommandValidator.isValidSearchCommand(args));
        assertTrue(SearchCommandValidator.isValidSearchCommand(args1));
        assertFalse(SearchCommandValidator.isValidSearchCommand(args));
    }

    @Test
    public void validEntityIdParameters() {
        String[] args = {"search", "X030927", "entityId=sdfsdf"};
        String[] args1 = {"search", "X030927", "entityId=12"};
        String[] args2 = {"search", "X030927", "entityId=-12"};
        String[] args3 = {"search", "X030927", "entityId=2500055380"};

        assertFalse(SearchCommandValidator.isValidSearchCommand(args));
        assertTrue(SearchCommandValidator.isValidSearchCommand(args1));
        assertFalse(SearchCommandValidator.isValidSearchCommand(args2));
        assertTrue(SearchCommandValidator.isValidSearchCommand(args3));
    }

    @Test
    public void validPageSizeParameters() {
        String[] args = {"search", "X030927", "pageSize=sdfsdf"};
        String[] args1 = {"search", "X030927", "pageSize=12"};
        String[] args3 = {"search", "X030927", "pageSize=-2"};

        assertFalse(SearchCommandValidator.isValidSearchCommand(args));
        assertTrue(SearchCommandValidator.isValidSearchCommand(args1));
        assertFalse(SearchCommandValidator.isValidSearchCommand(args3));
    }

    @Test
    public void validStartIndexParameters() {
        String[] args = {"search", "X030927", "startIndex=sdfsdf"};
        String[] args1 = {"search", "X030927", "startIndex=12"};
        String[] args2 = {"search", "X030927", "startIndex=-12"};
        assertFalse(SearchCommandValidator.isValidSearchCommand(args));
        assertTrue(SearchCommandValidator.isValidSearchCommand(args1));
        assertFalse(SearchCommandValidator.isValidSearchCommand(args2));
    }

    @Test
    public void validStartDateParameters() {
        String[] args = {"search", "X030927", "from=sdfsdf"};
        String[] args1 = {"search", "X030927", "from=2013/10/22"};
        String[] args2 = {"search", "X030927", "from=2013-10-22"};
        String[] args3 = {"search", "X030927", "from=10-12-2016"};

        assertFalse(SearchCommandValidator.isValidSearchCommand(args));
        assertFalse(SearchCommandValidator.isValidSearchCommand(args1));
        assertTrue(SearchCommandValidator.isValidSearchCommand(args2));
        assertFalse(SearchCommandValidator.isValidSearchCommand(args3));
    }

    @Test
    public void validEndDateParameters() {
        String[] args = {"search", "X030927", "to=sdfsdf"};
        String[] args1 = {"search", "X030927", "to=2013/10/22"};
        String[] args2 = {"search", "X030927", "to=2013-10-22"};
        String[] args3 = {"search", "X030927", "to=10-12-2016"};

        assertFalse(SearchCommandValidator.isValidSearchCommand(args));
        assertFalse(SearchCommandValidator.isValidSearchCommand(args1));
        assertTrue(SearchCommandValidator.isValidSearchCommand(args2));
        assertFalse(SearchCommandValidator.isValidSearchCommand(args3));
    }

    @Test
    public void validDocTypeParameters() {
        String[] args = {"search", "X030927", "docType=sdfsdf"};
        String[] args1 = {"search", "X030927", "docType=12"};
        String[] args2 = {"search", "X030927", "docType=DOCUMENT"};
        String[] args3 = {"search", "X030927", "docType=PREVIOUS_CONVICTION"};

        assertFalse(SearchCommandValidator.isValidSearchCommand(args));
        assertFalse(SearchCommandValidator.isValidSearchCommand(args1));
        assertTrue(SearchCommandValidator.isValidSearchCommand(args2));
        assertTrue(SearchCommandValidator.isValidSearchCommand(args3));
    }

    @Test
    public void validEntityTypeParameters() {
        String[] args = {"search", "X030927", "etityType"};
        String[] args1 = {"search", "X030927", "etityType="};
        String[] args2 = {"search", "X030927", "entityType=APREFERRAL"};
        String[] args3 = {"search", "X030927", "entityType=REFERRAL"};

        assertFalse(SearchCommandValidator.isValidSearchCommand(args));
        assertFalse(SearchCommandValidator.isValidSearchCommand(args1));

        assertTrue(SearchCommandValidator.isValidSearchCommand(args2));
        assertTrue(SearchCommandValidator.isValidSearchCommand(args3));
    }


    @Test
    public void validValidCommand() {
        String[] args = {"search", "from"};
        String[] args1 = {"search", "entityId"};
        String[] args2 = {"search", "X030927"};
        String[] args3 = {"search", "xvxc"};

        assertFalse(SearchCommandValidator.isValidSearchCommand(args));
        assertFalse(SearchCommandValidator.isValidSearchCommand(args1));
        assertTrue(SearchCommandValidator.isValidSearchCommand(args2));
        assertFalse(SearchCommandValidator.isValidSearchCommand(args3));
    }

    @Test
    public void validSortParameters() {
        String[] args = {"search", "X030927", "sort=docType"};
        String[] args1 = {"search", "X030927", "sort=entityType"};
        String[] args2 = {"search", "X030927", "sort=entityId"};
        String[] args3 = {"search", "X030927", "sort=fileSize"};
        String[] args4 = {"search", "X030927", "sort=lastModified"};
        String[] args5 = {"search", "X030927", "sort=fileExtension"};

        String[] args6 = {"search", "X030927", "sort=test"};
        String[] args7 = {"search", "X030927", "sort=from"};
        String[] args8 = {"search", "X030927", "sort=maxResults"};

        assertTrue(SearchCommandValidator.isValidSearchCommand(args));
        assertTrue(SearchCommandValidator.isValidSearchCommand(args1));
        assertTrue(SearchCommandValidator.isValidSearchCommand(args2));
        assertTrue(SearchCommandValidator.isValidSearchCommand(args3));
        assertTrue(SearchCommandValidator.isValidSearchCommand(args4));
        assertTrue(SearchCommandValidator.isValidSearchCommand(args5));

        assertFalse(SearchCommandValidator.isValidSearchCommand(args6));
        assertFalse(SearchCommandValidator.isValidSearchCommand(args7));
        assertFalse(SearchCommandValidator.isValidSearchCommand(args8));
    }

    @Test
    public void validCRNParameters() {
        String[] args = {"search", "11", "sort=entityType"};
        String[] args1 = {"search", "X030927", "sort=entityType"};
        String[] args2 = {"search", "33", "sort=entityType"};
        String[] args3 = {"search", "test", "sort=entityType"};
        String[] args4 = {"search", "999999", "sort=entityType"};
        String[] args5 = {"search", "00VBGY", "sort=entityType"};

        assertFalse(SearchCommandValidator.isValidSearchCommand(args));
        assertTrue(SearchCommandValidator.isValidSearchCommand(args1));
        assertFalse(SearchCommandValidator.isValidSearchCommand(args2));
        assertFalse(SearchCommandValidator.isValidSearchCommand(args3));
        assertFalse(SearchCommandValidator.isValidSearchCommand(args4));
        assertFalse(SearchCommandValidator.isValidSearchCommand(args5));
    }
}
