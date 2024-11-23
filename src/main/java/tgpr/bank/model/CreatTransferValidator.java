package tgpr.bank.model;

import tgpr.framework.Error;
import tgpr.framework.ErrorList;
import tgpr.framework.Tools;
import java.util.regex.Pattern;


public abstract class CreatTransferValidator {

    public static Error isValidIban(String iban1, String iban2) {
        if (iban1 == null || iban1.isBlank())
            return new Error("iban required",Account.field.iban);
        if (!Pattern.matches("[A-Za-z]{2}[0-9]{2} [0-9]{4} [0-9]{4} [0-9]{4}",iban1))
            return new Error("invalid iban.",Account.field.iban);
        if (iban1.compareToIgnoreCase(iban2) == 0)
            return new Error("must be different of source account",Account.field.iban);
     return Error.NOERROR;
    }


    public static Error isValidTitle(String title) {
        if (title == null || title.isBlank())
            return new Error("title required", Account.field.title);
        if (!Pattern.matches("[A-Za-z0-9 ]{3,20}",title))
            return new Error("minimum 3 character", Account.field.title);
        return Error.NOERROR;
    }

//si maxAmount est 0 et que date.text est invalid alors
    public static Error isValidAmount(String amount, double maxAmount, String date) {
        if (maxAmount == 0 && !Tools.isValidDate(date))
            return new Error("floor reached, creat a transfer to the future",Transfer.field.amount);
        else {
            if (amount == null || amount.isBlank())
                return new Error("amount required", Transfer.field.amount);
            if (Tools.toDouble(amount) <= 0)
                return new Error("amount must > 0 €",Transfer.field.amount);
            if (Tools.toDouble(amount) > maxAmount && date.equalsIgnoreCase(""))
                return new Error("amount must be <= " + maxAmount + " €", Transfer.field.amount);
        }
        return Error.NOERROR;

    }

    public static Error isValidDescription(String description) {
        if (description == null || description.isBlank())
            return new Error("description required", Transfer.field.description);
        return Error.NOERROR;
    }

    public static Error isValidDate(String date) {

        if (date.compareTo("") != 0) {
            if (!Tools.isValidDate(date))
                return new Error("invalid date or format => (12/12/2000)", Transfer.field.created_at);

            Global systemDate = Global.getSystemDateRow();
            if (Tools.toDate(date).isBefore(systemDate.getSystemDate().toLocalDate()) || Tools.toDate(date).isEqual(systemDate.getSystemDate().toLocalDate()))
                return new Error("date must be after current date", Transfer.field.created_at);
        }
        return Error.NOERROR;
    }


    public static ErrorList validate(String ibanTxtIban, String cmbxIbanSource, String title, String amount, double maxAmount, String description, String date) {
        var errors = new ErrorList();

        errors.add(isValidIban(ibanTxtIban,cmbxIbanSource));
        errors.add(isValidTitle(title));
        errors.add(isValidAmount(amount,maxAmount,date));
        errors.add(isValidDescription(description));
        errors.add(isValidDate(date));

        return errors;
    }
}
