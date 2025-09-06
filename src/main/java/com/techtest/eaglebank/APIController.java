package com.techtest.eaglebank;

import java.time.OffsetDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baeldung.openapi.api.V1ApiController;
import com.techtest.eaglebank.delegates.ApiDelegate;

@Service
public class APIController extends V1ApiController {

    public APIController(@Autowired(required = true) ApiDelegate delegate) {
        super(delegate);
    }
}
