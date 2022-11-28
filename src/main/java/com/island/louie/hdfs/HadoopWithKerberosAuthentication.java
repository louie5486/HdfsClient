package com.island.louie.hdfs;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.security.UserGroupInformation;
import org.ietf.jgss.GSSException;

public class HadoopWithKerberosAuthentication {

    public static void main(String[] args) throws IOException, GSSException {

        // set kerberos host and realm
        System.setProperty("java.security.krb5.realm", "ISLAND.COM");
        System.setProperty("java.security.krb5.kdc", "192.168.56.151");

        Configuration conf = new Configuration();

        conf.set("hadoop.security.authentication", "kerberos");
        conf.set("hadoop.security.authorization", "true");

        conf.set("fs.defaultFS", "hdfs://cdh2.cub.com.tw");
        conf.set("fs.hdfs.impl", DistributedFileSystem.class.getName());

        // hack for running locally with fake DNS records
        // set this to true if overriding the host name in /etc/hosts
        conf.set("dfs.client.use.datanode.hostname", "true");

        // server principal
        // the kerberos principle that the namenode is using
        conf.set("dfs.namenode.kerberos.principal.pattern", "hdfs/*@ISLAND.COM");
        UserGroupInformation.setConfiguration(conf);
        UserGroupInformation.loginUserFromKeytab("op002@ISLAND.COM", "/Users/louie/op002.keytab");

        FileSystem fs = FileSystem.get(conf);
        RemoteIterator<LocatedFileStatus> files = fs.listFiles(new Path("/user/louie"), true);
        while(files.hasNext()) {
            LocatedFileStatus file = files.next();

            System.out.println(file.getPath());
        }
    }
}
