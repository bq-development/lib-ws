package io.corbel.lib.ws.model;

public abstract class ModelValidatorTestBase {

    protected static final String TEST_ANYFIELD = "TEST_ANYFIELD";
    protected static final String TEST_NOTEMPTY = "TEST_NOTEMPTY";
    protected static final String TEST_NOTBLANK = "TEST_NOTBLANK";
    protected static final String TEST_NOTEMPTYANDEMAILTYPE = "TEST_NOTEMPTYANDEMAILTYPE@test.com";

    protected ModelValidatorTestBaseModel getNewTestModel() {
        return new ModelValidatorTestBaseModel();
    }
}
