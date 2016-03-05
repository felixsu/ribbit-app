package felix.com.ribbit.model;

import felix.com.ribbit.exception.InputValidityException;

/**
 * Created by fsoewito on 3/5/2016.
 */
public interface Validatable {

    void validate() throws InputValidityException;
}
