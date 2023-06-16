package com.unyt.unytbankapp.service.impl;

import com.unyt.unytbankapp.dto.EmailDetails;

public interface EmailService {

    void sendEmailAlert (EmailDetails emailDetails);
}
