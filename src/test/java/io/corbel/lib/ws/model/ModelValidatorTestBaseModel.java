package io.corbel.lib.ws.model;


import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

public class ModelValidatorTestBaseModel {

    private String anyValueField;

    @NotEmpty
    private String notemptyField;

    @NotEmpty
    @Email
    private String notEmptyAndEmailTypeField;

    @NotBlank
    private String notBlankField;

    public String getNotBlankField() {
        return notBlankField;
    }

    public void setNotBlankField(String notBlankField) {
        this.notBlankField = notBlankField;
    }

    public String getAnyValueField() {
        return anyValueField;
    }

    public void setAnyValueField(String anyValueField) {
        this.anyValueField = anyValueField;
    }

    public String getNotemptyField() {
        return notemptyField;
    }

    public void setNotemptyField(String notemptyField) {
        this.notemptyField = notemptyField;
    }

    public String getNotEmptyAndEmailTypeField() {
        return notEmptyAndEmailTypeField;
    }

    public void setNotEmptyAndEmailTypeField(String notEmptyAndEmailTypeField) {
        this.notEmptyAndEmailTypeField = notEmptyAndEmailTypeField;
    }
}
