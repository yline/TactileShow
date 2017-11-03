package com.concurrent.yline;

import com.yline.utils.LogUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;

public class ModelDaoUtil {
    public static byte[] objectToByte(Object object) throws NotSerializableException {
        if (null != object) { //  && object instanceof Serializable
            ByteArrayOutputStream baoStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = null;
            try {
                objectOutputStream = new ObjectOutputStream(baoStream);

                objectOutputStream.writeObject(object);

                return baoStream.toByteArray();
            } catch (NotSerializableException e) {
                throw e;
            } catch (IOException e) {
                LogUtil.e("NetCacheModelDao objectToByte", e);
            } catch (Throwable e) {
                LogUtil.e("NetCacheModelDao objectToByte", e);
            } finally {
                try {
                    if (null != objectOutputStream) {
                        objectOutputStream.close();
                    }
                    baoStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
