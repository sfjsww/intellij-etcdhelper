package com.github.tsonglew.etcdhelper.common;

import io.etcd.jetcd.*;
import io.etcd.jetcd.auth.Permission;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.vertx.core.cli.CLI;

import javax.swing.plaf.synth.SynthCheckBoxMenuItemUI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author tsonglew
 */
public class EtcdClient {

    private static final byte[] NO_PREFIX_END = new byte[]{0};
    private Client client;
    private KV kvClient;
    private Auth authClient;
    private Lease leaseClient;
    public String[] endpoints;

    private static final boolean INIT = false;

    private static ByteSequence prefixEndOf(ByteSequence prefix) {
        byte[] endKey = prefix.getBytes().clone();

        for (int i = endKey.length - 1; i >= 0; --i) {
            if (endKey[i] != -1) {
                ++endKey[i];
                return ByteSequence.from(Arrays.copyOf(endKey, i + 1));
            }
        }

        return ByteSequence.from(NO_PREFIX_END);
    }

    private static String prefixEndOf(String prefix) {
        return prefixEndOf(bytesOf(prefix)).toString();
    }

    private static ByteSequence bytesOf(String s) {
        return ByteSequence.from(StringUtils.string2Bytes(s));
    }

    /**
     * 角色是否存在
     *
     * @param role 角色名
     * @return 是否存在
     */
    private boolean roleExists(String role) {
        try {
            return authClient.roleGet(bytesOf(role)).get() != null;
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 添加角色，并绑定读写权限
     *
     * @param role            角色名
     * @param readWritePrefix 可读写的前缀
     * @return 添加成功
     */
    public boolean roleAdd(String role, String readWritePrefix) {
        try {
            if (!roleExists(role)) {
                authClient.roleAdd(bytesOf(role)).get().toString();
            }
            authClient.roleGrantPermission(
                    bytesOf(role),
                    bytesOf(readWritePrefix),
                    prefixEndOf(bytesOf(readWritePrefix)),
                    Permission.Type.READWRITE).get();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除角色
     *
     * @param role 角色名
     * @return 删除成功
     */
    public boolean roleDelete(String role) {
        try {
            authClient.roleDelete(bytesOf(role)).get();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 用户是否存在
     *
     * @param user 用户名
     * @return 是否存在
     */
    private boolean userExists(String user) {
        try {
            return authClient.userGet(bytesOf(user)).get() != null;
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 添加用户
     *
     * @param user 用户名
     * @param pwd  用户密码
     * @return 创建成功
     */
    public boolean userAdd(String user, String pwd) {
        try {
            if (!userExists(user)) {
                authClient.userAdd(bytesOf(user), bytesOf(pwd)).get();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除用户
     *
     * @param user 用户名
     * @return 删除成功
     */
    public boolean userDelete(String user) {
        try {
            authClient.userDelete(bytesOf(user)).get();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean userGrantRole(String user, String role) {
        try {
            authClient.userGrantRole(bytesOf(user), bytesOf(role));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<String> userList() {
        try {
            return authClient.userList().get().getUsers();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return List.of();
    }

    public List<String> roleList() {
        try {
            return authClient.roleList().get().getRoles();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return List.of();
    }

    public void init(String[] etcdUrls, String user, String password) {
        this.endpoints = etcdUrls;
        ClientBuilder clientBuilder = Client.builder().endpoints(etcdUrls);
        if (user != null && password != null) {
            clientBuilder.user(bytesOf(user)).password(bytesOf(password));
        }
        client = clientBuilder.build();
        kvClient = client.getKVClient();
        authClient = client.getAuthClient();
        leaseClient = client.getLeaseClient();
    }

    public void close() {
        kvClient.close();
        authClient.close();
        leaseClient.close();
        client.close();
    }

    /**
     * 写入数据
     *
     * @param key           key
     * @param value         value
     * @param timeoutMillis 写入的操作的 timeout
     * @return 是否写入成功
     */
    public boolean put(String key, String value, int timeoutMillis, int ttlSecs) {
        if (ttlSecs > 0) {
            return putWithTtl(key, value, timeoutMillis, ttlSecs);
        }
        try {
            kvClient.put(bytesOf(key), bytesOf(value)).get(timeoutMillis, TimeUnit.MILLISECONDS);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean putWithTtl(String key, String value, int timeoutMillis, int ttlSecs) {
        try {
            long leaseId = leaseClient.grant(ttlSecs).get().getID();
            kvClient.put(
                    bytesOf(key),
                    bytesOf(value),
                    PutOption.newBuilder().withLeaseId(leaseId).build()).get(timeoutMillis, TimeUnit.MILLISECONDS);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(String key) {
        try {
            kvClient.delete(bytesOf(key)).get();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<KeyValue> getByPrefix(String key, Integer limit) {
        try {
            GetOption.Builder optionBuilder = GetOption.newBuilder()
                    .withRange(prefixEndOf(bytesOf(key)))
                    .withSortField(GetOption.SortTarget.MOD)
                    .withSortOrder(GetOption.SortOrder.ASCEND);
            if (limit != null && limit > 0) {
                optionBuilder.withLimit(limit);
            }
            return kvClient.get(bytesOf(key), optionBuilder.build()).get().getKvs();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return List.of();
    }
}
