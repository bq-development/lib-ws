package io.corbel.lib.ws.model;


import org.fest.assertions.api.Assertions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolationException;

public class ModelValidatorTest extends ModelValidatorTestBase {

    private ModelValidatorTestBaseModel model;

    @Before
    public void setUp() {
        model = getNewTestModel();
    }

    @Test
    public void testNewModel() {
        try {
            ModelValidator.validateObject(model);
            Assert.fail();
        } catch (ConstraintViolationException c) {
            Assertions.assertThat(c.getConstraintViolations().size()).isEqualTo(3);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testModelAnyValueField() {
        try {
            model.setAnyValueField(TEST_ANYFIELD);
            ModelValidator.validateObject(model);
            Assert.fail();
        } catch (ConstraintViolationException c) {
            Assertions.assertThat(c.getConstraintViolations().size()).isEqualTo(3);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testFilledFieldButNotValid() {
        try {
            model.setAnyValueField(TEST_ANYFIELD);
            model.setNotemptyField(TEST_NOTEMPTY);
            model.setNotBlankField(TEST_NOTBLANK);
            model.setNotEmptyAndEmailTypeField(TEST_NOTEMPTY);
            ModelValidator.validateObject(model);
            Assert.fail();
        } catch (ConstraintViolationException c) {
            Assertions.assertThat(c.getConstraintViolations().size()).isEqualTo(1);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testCorrectCase() {
        try {
            model.setAnyValueField(TEST_ANYFIELD);
            model.setNotemptyField(TEST_NOTEMPTY);
            model.setNotBlankField(TEST_NOTBLANK);
            model.setNotEmptyAndEmailTypeField(TEST_NOTEMPTYANDEMAILTYPE);
            ModelValidator.validateObject(model);
        } catch (Exception e) {
            Assert.fail();
        }
    }
}
