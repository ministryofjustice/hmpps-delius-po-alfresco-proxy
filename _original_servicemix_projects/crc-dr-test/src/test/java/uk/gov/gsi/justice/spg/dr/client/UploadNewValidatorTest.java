package uk.gov.gsi.justice.spg.dr.client;

import org.junit.Test;
import uk.gov.gsi.justice.spg.dr.client.validators.UploadNewValidator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UploadNewValidatorTest {


    @Test
    public void validCrnParameters() {
        String[] args = {"uploadnew", "X030927", "test.doc", "author=test", "entityType=REFERRAL" , "entityId=1", "docType=DOCUMENT"};
        String[] args1 = {"uploadnew", "X030927123123", "test.doc","author=test", "entityType=REFERRAL" , "entityId=1", "docType=DOCUMENT"};
        String[] args2 = {"uploadnew", "1234", "test.doc" , "author=test", "entityType=REFERRAL" , "entityId=1", "docType=DOCUMENT"};
        String[] args3 = {"uploadnew", "test" , "test.doc" , "author=test", "entityType=REFERRAL" , "entityId=1", "docType=DOCUMENT"};
        String[] args4 = {"uploadnew", "test.doc", "author=test", "entityType=REFERRAL" , "entityId=1", "docType=DOCUMENT"};

        assertTrue(UploadNewValidator.isValidUploadNewCommand(args));

        assertFalse(UploadNewValidator.isValidUploadNewCommand(args1));
        assertFalse(UploadNewValidator.isValidUploadNewCommand(args2));
        assertFalse(UploadNewValidator.isValidUploadNewCommand(args3));
        assertFalse(UploadNewValidator.isValidUploadNewCommand(args4));
    }

    @Test
    public void validEntityTypeParameters() {
        String[] args = {"uploadnew", "X030927", "test.doc","author=test", "etityType", "entityId=1", "docType=DOCUMENT"};
        String[] args1 = {"uploadnew", "X030927", "test.doc","author=test", "etityType=", "entityId=1", "docType=DOCUMENT"};
        String[] args2 = {"uploadnew", "X030927", "test.doc","author=test", "entityType=APREFERRAL", "entityId=1", "docType=DOCUMENT"};
        String[] args3 = {"uploadnew", "X030927", "test.doc","author=test", "entityType=REFERRAL", "entityId=1", "docType=DOCUMENT"};

        assertFalse(UploadNewValidator.isValidUploadNewCommand(args));
        assertFalse(UploadNewValidator.isValidUploadNewCommand(args1));

        assertTrue(UploadNewValidator.isValidUploadNewCommand(args2));
        assertTrue(UploadNewValidator.isValidUploadNewCommand(args3));
    }

    @Test
    public void validentityIdParameters() {
        String[] args = {"uploadnew", "X030927", "test.doc","author=test", "entityType=APREFERRAL","entityId=1", "docType=DOCUMENT"};
        String[] args1 = {"uploadnew","X030927",  "test.doc","author=test", "entityType=APREFERRAL","entityId=test", "docType=DOCUMENT"};
        String[] args2 = {"uploadnew", "X030927", "test.doc","author=test", "entityType=APREFERRAL", "entityId=1123123213123213", "docType=DOCUMENT"};

        assertTrue(UploadNewValidator.isValidUploadNewCommand(args));

        assertFalse(UploadNewValidator.isValidUploadNewCommand(args1));
        assertTrue(UploadNewValidator.isValidUploadNewCommand(args2));
    }

    @Test
    public void validDocTypeParameters() {
        String[] args = {"uploadnew", "X030927", "test.doc","author=test", "entityType=APREFERRAL","entityId=1","docType=1"};
        String[] args1 = {"uploadnew", "X030927", "test.doc","author=test", "entityType=APREFERRAL","entityId=1","docType=DOCUMENT"};
        String[] args2 = {"uploadnew", "X030927", "test.doc","author=test", "entityType=APREFERRAL","entityId=1","docType=CPS_PACK"};

        assertFalse(UploadNewValidator.isValidUploadNewCommand(args));

        assertTrue(UploadNewValidator.isValidUploadNewCommand(args1));
        assertTrue(UploadNewValidator.isValidUploadNewCommand(args2));
    }

    @Test
    public void validLockedParameters() {
        String[] args = {"uploadnew", "X030927","author=test", "entityType=APREFERRAL","entityId=1","docType=DOCUMENT","locked=1"};
        String[] args1 = {"uploadnew", "X030927","author=test", "entityType=APREFERRAL","entityId=1","docType=DOCUMENT","locked=true"};
        String[] args2 = {"uploadnew", "X030927","author=test", "entityType=APREFERRAL","entityId=1","docType=DOCUMENT","locked=false"};

        assertFalse(UploadNewValidator.isValidUploadNewCommand(args));

        assertTrue(UploadNewValidator.isValidUploadNewCommand(args1));
        assertTrue(UploadNewValidator.isValidUploadNewCommand(args2));
    }

    @Test
    public void validReservedParameters() {
        String[] args = {"uploadnew", "X030927","author=test", "entityType=APREFERRAL","entityId=1","docType=DOCUMENT","reserved=1"};
        String[] args1 = {"uploadnew", "X030927","author=test", "entityType=APREFERRAL","entityId=1","docType=DOCUMENT","reserved=true"};
        String[] args2 = {"uploadnew", "X030927","author=test", "entityType=APREFERRAL","entityId=1","docType=DOCUMENT","reserved=false"};

        assertFalse(UploadNewValidator.isValidUploadNewCommand(args));

        assertTrue(UploadNewValidator.isValidUploadNewCommand(args1));
        assertTrue(UploadNewValidator.isValidUploadNewCommand(args2));
    }
}
