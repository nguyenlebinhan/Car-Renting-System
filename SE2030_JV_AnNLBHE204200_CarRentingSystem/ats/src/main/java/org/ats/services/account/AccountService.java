package org.ats.services.account;

import org.ats.security.request.RegisterRequest;

public interface AccountService {
    Boolean register(RegisterRequest request);
}
