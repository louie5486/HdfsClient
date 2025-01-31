package fr.tony.hdfs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import javax.security.auth.login.LoginException;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Before;
import org.junit.Test;

import com.tony.hdfs.HDFSClientKerberos;

public class HDFSClientKerberosTest {

	private HDFSClientKerberos client;
	
	@Before
	public void prepare() throws IOException {
        Properties prop = new Properties();

        ClassLoader classLoader = getClass().getClassLoader();
        
        InputStream input = classLoader.getResourceAsStream("hadoop.properties");
        prop.load(input);

		client = new HDFSClientKerberos();
		
		client.setHadoopCluster(prop.getProperty("hadoop.cluster"));
		client.setNamenodes(prop.getProperty("hadoop.namenodes"));
		client.setHttpAaddress(prop.getProperty("hadoop.httpAddress"));
		client.setRpcAddress(prop.getProperty("hadoop.rpcAddress"));
		client.setHadoopProxy(prop.getProperty("hadoop.failoverProxy"));
		
		URL jaas = classLoader.getResource(prop.getProperty("hadoop.jaasConfUrl"));
		URL krb5 = classLoader.getResource(prop.getProperty("hadoop.krb5Url"));
		client.setJaasConfUrl(jaas);
		client.setKrbConfUrl(krb5);
	}
	
	@Test
	public void testLoginWithKeytab() throws NoSuchAlgorithmException, URISyntaxException, IOException {
		String keytabPath = getClass().getClassLoader().getResource("op002_n.keytab").getPath();
		FileSystem fs = client.hadoopConnectionWithKeytab(keytabPath, "op002@ISLAND.COM");
		list(fs);
	}

	@Test
	public void testLoginWithPassword() throws NoSuchAlgorithmException, LoginException, URISyntaxException, IOException {
		FileSystem fs = client.hadoopConnectionWithUserPassword("xxx@xxx.CORP", "xxxxx");
		list(fs);
	}
	
	private void list(FileSystem fs) throws FileNotFoundException, IllegalArgumentException, IOException {
		String pathStr = "/user";
		System.out.println("Listing files in: " + pathStr);
		FileStatus[] fsStatus = fs.listStatus(new Path(pathStr));

		for (int i = 0; i < fsStatus.length; i++) {
			System.out.println(fsStatus[i].getPath().toString());
		}
	}
	
}
