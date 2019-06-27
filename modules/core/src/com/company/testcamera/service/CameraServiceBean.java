package com.company.testcamera.service;

import com.company.testcamera.entity.Camera;
import com.haulmont.bali.db.MapListHandler;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Query;
import com.haulmont.cuba.core.Transaction;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@Service(CameraService.NAME)
public class CameraServiceBean implements CameraService {

    @Inject
    private Persistence persistence;

    Process p =null;


}
