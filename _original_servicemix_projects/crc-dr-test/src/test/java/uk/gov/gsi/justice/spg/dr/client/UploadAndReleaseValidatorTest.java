package uk.gov.gsi.justice.spg.dr.client;

import org.junit.Test;
import uk.gov.gsi.justice.spg.dr.client.validators.UploadAndReleaseValidator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UploadAndReleaseValidatorTest {

    @Test
    public void validNumberParameters() {
        String[] args = {"uploadandrelease", "testDocId", "test.doc", "author=test"};
        String[] args1 = {"uploadandrelease", "testDocId", "test.doc"};
        String[] args2 = {"uploadandrelease", "testDocId", "test.doc", "author=test","entityType=REFERRAL"};

        assertTrue(UploadAndReleaseValidator.isValidUploadAndRelease(args));
        assertFalse(UploadAndReleaseValidator.isValidUploadAndRelease(args1));
        assertTrue(UploadAndReleaseValidator.isValidUploadAndRelease(args2));
    }

    @Test
    public void validEntityTypeParameters() {
        String[] args = {"uploadandrelease", "testDocId", "test.doc", "author=test" , "entityType=REFERRAL"};
        String[] args1 = {"uploadandrelease", "testDocId", "test.doc", "author=test" , "entityType="};
        String[] args2 = {"uploadandrelease", "testDocId", "test.doc", "author=test","entityType=REFL"};

        assertTrue(UploadAndReleaseValidator.isValidUploadAndRelease(args));
        assertFalse(UploadAndReleaseValidator.isValidUploadAndRelease(args1));
        assertFalse(UploadAndReleaseValidator.isValidUploadAndRelease(args2));
    }

    @Test
    public void validEntityIdParameters() {
        String[] args = {"uploadandrelease", "testDocId", "test.doc", "author=test" , "entityId=1"};
        String[] args1 = {"uploadandrelease", "testDocId", "test.doc", "author=test" , "entityId=test"};

        assertTrue(UploadAndReleaseValidator.isValidUploadAndRelease(args));
        assertFalse(UploadAndReleaseValidator.isValidUploadAndRelease(args1));
    }

    @Test
    public void validDocTypeParameters() {
        String[] args = {"uploadandrelease", "testDocId", "test.doc", "author=test" , "docType=DOCUMENT"};
        String[] args1 = {"uploadandrelease", "testDocId", "test.doc", "author=test" , "docType=test"};
        String[] args2 = {"uploadandrelease", "testDocId", "test.doc", "author=test" , "docType=1"};

        assertTrue(UploadAndReleaseValidator.isValidUploadAndRelease(args));
        assertFalse(UploadAndReleaseValidator.isValidUploadAndRelease(args1));
        assertFalse(UploadAndReleaseValidator.isValidUploadAndRelease(args2));
    }
}
