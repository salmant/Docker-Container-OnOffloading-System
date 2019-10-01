// ------------------------------------------------------------------------
// Author: Salman Taherizadeh - Jozef Stefan Institute (JSI)
// ------------------------------------------------------------------------
// Execution: java On_Offloading_Client
// ------------------------------------------------------------------------

import java.io.*;
import java.net.*;

public class On_Offloading_Client extends Thread implements Runnable {
	
	public static void main(String arg[]) throws Exception {
		ServerSocket welcomeSocket = new ServerSocket(10001); // listening port to receive command from on_off_loading_server
		try {
			while (true){
				String return_value = "";
				String on_off_loading_server_ip = "";
				int inputs_count = 0;
				int ports_count = 0;
				Socket connectionSocket = welcomeSocket.accept();
				BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
				String clientSentence;
				///////////////
				while ((clientSentence = inFromClient.readLine())!= null){
					//System.out.println(clientSentence);
					String[] argv = clientSentence.split(",");
					String ContainerImageName = argv[1];
					String IP = argv[2];
					
					
					if ((argv[0].equals("Instantiation"))||(argv[0].equals("instantiation"))){
						
						for (int i=0; argv[3+(i*3)].equals("tcp") || argv[3+(i*3)].equals("udp"); i++) ports_count++;
						String ContainerPort[] = new String[ports_count];
						String HostPort[] = new String[ports_count];
						String tcp_or_udp[] = new String[ports_count];
						for (int i=0; i<ports_count; i++) {
								tcp_or_udp[i] = argv[3+(i*3)];
								ContainerPort[i] = argv[4+(i*3)];
								HostPort[i] = argv[5+(i*3)];
						}
						
						
						String input_name[] = new String[(int)((argv.length - 3 - (ports_count * 3))/2)]; 
						String input_value[] = new String[(int)((argv.length - 3 - (ports_count * 3))/2)]; 
						
						for (int i=(3 + (ports_count * 3)); i < argv.length; i=i+2) if (argv[i]!=null){
							input_name[inputs_count] = argv[i];
							input_value[inputs_count] = argv[i+1];
							inputs_count++;
						}
						
						String Id = create_a_container(IP, ContainerImageName, ContainerPort, HostPort, tcp_or_udp, input_name, input_value, inputs_count, ports_count);
						if (Id==null || Id.trim().equals("")) return_value = "9201"; //System.out.println("9201"); // Code of error in start a container: error in create_a_container()
						else {
							String Result = start_a_container(Id, IP);
							if (!(Result==null || Result.trim().equals(""))) return_value = "9202"; //System.out.println("9202"); // Code of error in start a container: error in start_a_container()
							else return_value = "9200"; //System.out.println("9200"); // Code of success in start a container
						}
					} else
					if ((argv[0].equals("Termination"))||(argv[0].equals("termination"))){
						String Id = get_container_id(ContainerImageName, IP);
						//System.out.println(Id);
						if (Id.equals("")) return_value = "9301"; //System.out.println("9301"); // Code of error in stop a container
						else {
							stop_a_container(Id, IP);
							return_value = "9300"; //System.out.println("9300"); // Code of success in stop a container
						}
					}
				//////start line: sending the return value to on_offloading_server if the command is successful or not
				on_off_loading_server_ip = connectionSocket.getRemoteSocketAddress().toString();
				on_off_loading_server_ip = on_off_loading_server_ip.substring(on_off_loading_server_ip.indexOf("/") + 1);
				on_off_loading_server_ip = on_off_loading_server_ip.substring(0, on_off_loading_server_ip.indexOf(":"));
				//System.out.println("------->" + on_off_loading_server_ip);
				//System.out.println("port 10002 on server is available");
				//System.out.println("return_value: " + return_value);
				Socket clientSocket1 = new Socket(on_off_loading_server_ip, 10002); // on_off_loading_server_ip (e.g. 194.249.1.175) and on_off_loading_server_port for the return value
				try{
					DataOutputStream outToServer1 = new DataOutputStream(clientSocket1.getOutputStream());
					outToServer1.writeBytes(return_value+ '\n');
				} finally {
					clientSocket1.close();
					break;
				}
				
				//////end line: sending the return value to on_offloading_server if the command is successful or not
				}
			}
		} finally {
			welcomeSocket.close();
		}
	}
	
	/////////////////////////////////////////////////////////////
	
	public static String create_a_container(String IP, String ContainerImageName, String ContainerPort[], String HostPort[], String tcp_or_udp[], String input_name[], String input_value[], int inputs_count, int ports_count){
        try{
			String mainCommand =
			"	curl -X POST -H \'Content-Type: application/json\' -d \'{	" +
			"	\"Image\":\"" + ContainerImageName + "\",	" ;
			if (inputs_count>0){
				mainCommand += "	\"Env\": [	" ;
				for (int i=0; i < inputs_count; i++) 
					if (i != inputs_count-1) mainCommand +="\"" + input_name[i] + "=" + input_value[i] + "\"," ;
					else mainCommand +="\"" + input_name[i] + "=" + input_value[i] + "\"" ;
				mainCommand += "		   ],	" ;
			}
			mainCommand +=
			"	\"Tty\": true,	" +
			"	\"ExposedPorts\": { " ;
			for (int i=0; i < ports_count; i++)
				if (i != ports_count-1) mainCommand += "\"" + ContainerPort[i] + "/" + tcp_or_udp[i] + "\": {},";
				else mainCommand += "\"" + ContainerPort[i] + "/" + tcp_or_udp[i] + "\": {}";
			mainCommand += "},	";
			mainCommand +=
			"	\"PortBindings\": { " ;
			for (int i=0; i < ports_count; i++)
				if (i != ports_count-1) mainCommand += "\"" + ContainerPort[i] + "/" + tcp_or_udp[i] + "\": [{ \"HostPort\": \"" + HostPort[i] + "\" }],";
				else mainCommand += "\"" + ContainerPort[i] + "/" + tcp_or_udp[i] + "\": [{ \"HostIp\": \"\",\"HostPort\": \"" + HostPort[i] + "\" }]";
			mainCommand += "}	" +
			"	}\' http://" + IP + ":4243/containers/create	"
			;
			//System.out.println("##############################################");
			//System.out.println(mainCommand);
			//System.out.println("##############################################");
			String[] command={"/bin/bash", "-c", mainCommand};
			Process P = Runtime.getRuntime().exec(command);
            P.waitFor();
            BufferedReader StdInput = new BufferedReader(new InputStreamReader(P.getInputStream()));
            String TopS ="";
			TopS= StdInput.readLine();
			TopS = TopS.substring(6);
			TopS = TopS.substring(TopS.indexOf("\"") + 1);
			TopS = TopS.substring(0, TopS.indexOf("\""));
			return TopS;
        }catch(Exception ex){
			ex.printStackTrace();
			return "";
        }
    }
	/////////////////////////////////////////////////////////////
	
	public static String start_a_container(String Id, String IP){
        try{
			String mainCommand = "curl -X POST http://" + IP + ":4243/containers/" + Id + "/start";
			//System.out.println("##############################################");
			//System.out.println(mainCommand);
			//System.out.println("##############################################");
			String[] command={"/bin/bash", "-c", mainCommand};
			Process P = Runtime.getRuntime().exec(command);
            P.waitFor();
            BufferedReader StdInput = new BufferedReader(new InputStreamReader(P.getInputStream()));
            String TopS ="";
			TopS= StdInput.readLine();
			return TopS;
        }catch(Exception ex){
			ex.printStackTrace();
			return "";
        }
    }
	/////////////////////////////////////////////////////////////
	
	public static String get_ListOfContainers(String IP){
		try{
			String mainCommand = "curl -X GET http://" + IP + ":4243/containers/json";
			String[] command={"/bin/bash", "-c", mainCommand};
			Process P = Runtime.getRuntime().exec(command);
			P.waitFor();
			BufferedReader Input = new BufferedReader(new InputStreamReader(P.getInputStream()));
			String ListOfContainers = Input.readLine();
			ListOfContainers = ListOfContainers.substring(1, ListOfContainers.length());
			return ListOfContainers;
		}catch(Exception ex){
			ex.printStackTrace();
			return "";
        }
	}
	/////////////////////////////////////////////////////////////
	
	public static int findClosingBracketMatchIndex(String str, int pos) {
		int depth = 1;
		for (int i = pos + 1; i < str.length(); i++) {
			switch (str.charAt(i)) {
				case '{':
					depth++;
					break;
				case '}':
					if (--depth == 0) {
						return i;
					}
					break;
			}
		}
		return -1; // No matching closing parenthesis
	}
	/////////////////////////////////////////////////////////////
	
	public static String get_container_id(String ContainerImageName, String IP){
		String ListOfContainers = get_ListOfContainers(IP);
		//System.out.println("ListOfContainers: " + ListOfContainers);
		int i=-1;
		while (i!=ListOfContainers.length()-1){
			i = findClosingBracketMatchIndex(ListOfContainers, 0);
			String tmp = ListOfContainers.substring(0, i+1);
			//String match = "\"PrivatePort\":" + PrivatePort + ",\"PublicPort\":" + PublicPort + ",\"Type\":\"" + Type + "\"";
			String match = ContainerImageName;
			if (tmp.contains(match)){
				tmp = tmp.substring(tmp.lastIndexOf("\"Id\":\"") + 5 + 1, tmp.lastIndexOf("\"Id\":\"") + 1 + 5 + 64);
				return tmp;
			}
			if (i==ListOfContainers.length()-1) break;
			ListOfContainers = ListOfContainers.substring(i+2, ListOfContainers.length());
		}
		return "";
	}
	/////////////////////////////////////////////////////////////
	
	public static void stop_a_container(String Id, String IP){
        try{
			String mainCommand = "curl -X POST http://" + IP + ":4243/containers/" + Id + "/stop";
			String[] command={"/bin/bash", "-c", mainCommand};
			Process P = Runtime.getRuntime().exec(command);
            P.waitFor();
            BufferedReader StdInput = new BufferedReader(new InputStreamReader(P.getInputStream()));
            String TopS ="";
			while((TopS= StdInput.readLine())!=null){}
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
	/////////////////////////////////////////////////////////////
	
	
}
