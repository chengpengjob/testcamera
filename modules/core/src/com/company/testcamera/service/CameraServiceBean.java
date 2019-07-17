package com.company.testcamera.service;


import com.company.testcamera.entity.Camera;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Query;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.core.sys.SecurityContext;
import com.haulmont.cuba.security.app.Authenticated;
import com.haulmont.cuba.security.app.Authentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service(CameraService.NAME)
public class CameraServiceBean implements CameraService {
    @Inject
    private Persistence persistence;
    //private final Logger log = LoggerFactory.getLogger(CameraService.class);


    public Camera getCameraById(Integer cameraId){
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            Query query = em.createQuery("select e from testcamera_Camera e where e.cameraId=:cameraId");
            query.setParameter("cameraId", cameraId);
            // query.setView(Space.class, "space-view");
            Camera camera = (Camera) query.getFirstResult();
            tx.commit();
            return camera;
        } catch (Exception e) {
            //log.error(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public  Camera updateCameraById(Integer cameraId,String videoAddress){
        Camera cameraById = getCameraById(cameraId);
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            cameraById.setVideoAddress(videoAddress);
            em.merge(cameraById);
            // query.setView(Space.class, "space-view");
            //Camera camera = (Camera) query.getFirstResult();
            tx.commit();
            //return camera;
        } catch (Exception e) {
            //log.error(e.getMessage());
            e.printStackTrace();
        }
        return null;

    }

    public Camera getCameraByNumber(String cameraNumber){
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            Query query = em.createQuery("select e from testcamera_Camera e where e.cameraNumber=:cameraNumber");
            query.setParameter("cameraNumber", cameraNumber);
            // query.setView(Space.class, "space-view");
            Camera camera = (Camera) query.getFirstResult();
            tx.commit();
            return camera;
        } catch (Exception e) {
            //log.error(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public  Camera updateCameraByNumber(String cameraNumber,String videoAddress){
        Camera cameraByNumber = getCameraByNumber(cameraNumber);
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            cameraByNumber.setVideoAddress(videoAddress);
            em.merge(cameraByNumber);
            // query.setView(Space.class, "space-view");
            //Camera camera = (Camera) query.getFirstResult();
            tx.commit();
            //return camera;
        } catch (Exception e) {
            //log.error(e.getMessage());
            e.printStackTrace();
        }
        return null;

    }

}

