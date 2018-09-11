package service.response;

import java.util.Objects;

/**
 * @author Hieu Pham
 * @since 9/6/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class RegistrationResponse {

    private String id;

    private boolean isDuplicate;

    public RegistrationResponse(String id, boolean isDuplicate) {
        this.id = id;
        this.isDuplicate = isDuplicate;
    }

    public String getId() {
        return id;
    }

    public boolean isDuplicate() {
        return isDuplicate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegistrationResponse that = (RegistrationResponse) o;
        return isDuplicate == that.isDuplicate &&
                Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, isDuplicate);
    }
}
