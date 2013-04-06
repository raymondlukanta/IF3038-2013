<%@page import="java.util.Map"%>
<%@ page import="java.io.*,javax.servlet.http.HttpServletRequest,javax.servlet.ServletInputStream" %>  
<%@ page import="java.io.FileWriter,java.io.IOException" %>  
<%
    String savePath = "", filepath = "", filename = "";
    String contentType = "", fileData = "", strLocalFileName = "";
    int startPos = 0, endPos = 0;
    int BOF = 0, EOF = 0;
%>  
<%!
    //copy specified number of bytes from main data buffer to temp data buffer  
    void copyByte(byte[] fromBytes, byte[] toBytes, int start, int len) {
        for (int i = start; i < (start + len); i++) {
            toBytes[i - start] = fromBytes[i];
        }
    }
%>  
<%
    contentType = request.getContentType();
    out.println("<br>Content type is :: " + contentType);
    if ((contentType != null) && (contentType.indexOf("multipart/form-data") >= 0)) {
        DataInputStream in = new DataInputStream(request.getInputStream());
        DataInputStream in1 = in;
        int formDataLength = request.getContentLength();
        byte dataBytes[] = new byte[formDataLength];
        int byteRead = 0;
        int totalBytesRead = 0;
        while (totalBytesRead < formDataLength) {
            byteRead = in1.read(dataBytes, totalBytesRead, formDataLength);
            totalBytesRead += byteRead;
        }
        // out.println("dooo<br>"+dataBytes.toString()+"EEEE");
        out.println("<br>totalBytesRead : " + totalBytesRead + "    :   formDataLength = " + formDataLength);

        String file = new String(dataBytes);
        out.println("<br>File Contents:<br>////////////////////////////////////<br>" + file + "<br>////////////////////////////////<br>");

        byte[] line = new byte[128];
        if (totalBytesRead < 3) {
            return;	//exit if file length is not sufficiently large  
        }

        String boundary = "";
        String s = "";
        int count = 0;
        int pos = 0;

        //loop for extracting boundry of file  
        //could also be extracted from request.getContentType()  
        do {
            copyByte(dataBytes, line, count, 1);	//read 1 byte at a time  
            count++;
            s = new String(line, 0, 1);
            fileData = fileData + s;
            pos = fileData.indexOf("Content-Disposition: form-data; name=\""); //set the file name  
            if (pos != -1) {
                endPos = pos + startPos;
            }
        } while (pos == -1);
        boundary = fileData.substring(startPos, endPos);
        //loop for extracting filename  
        int i = 0;
        int innerIter = 0;
        System.out.println("COUNT:" + count);
        do {
            innerIter++;
            startPos = endPos + (i * 159);
            // System.out.println("Start1:"+ startPos);
            copyByte(dataBytes, line, count, 1);	//read 1 byte at a time  
            count += 1;
            s = new String(line, 0, 1);
            fileData = fileData + s;
            pos = fileData.indexOf("filename=\"", startPos); //set the file name  
            if (pos != -1) {
                startPos = pos;
            }
        } while ((pos == -1));// && (innerIter <= 50 +i));
        System.out.println("Start2:" + startPos);
        System.out.println("innerIter" + innerIter);
        System.out.println("COUNTy:" + count);

        //out.println("<br>FILEDATA filename:"+ fileData);                      
        do {
            copyByte(dataBytes, line, count, 1);	//read 1 byte at a time  
            count += 1;
            s = new String(line, 0, 1);
            fileData = fileData + s;
            pos = fileData.indexOf("Content-Type: ", startPos);
            if (pos != -1) {
                endPos = pos;
            }
        } while (pos == -1);
        //out.println("<br>FILEDATA content-type:"+ fileData);                      

        filename = fileData.substring(startPos + 10, endPos - 3);	//to eliminate " from start & end  
        strLocalFileName = filename;
        int index = filename.lastIndexOf("\\");
        if (index != -1) {
            filename = filename.substring(index + 1);
        } else {
            filename = filename;
        }

        //loop for extracting ContentType  
        boolean blnNewlnFlag = false;
        startPos = endPos;	//added length of "Content-Type: "

        do {
            copyByte(dataBytes, line, count, 1);	//read 1 byte at a time  
            count += 1;
            s = new String(line, 0, 1);
            fileData = fileData + s;
            pos = fileData.indexOf("\n", startPos);
            if (pos != -1) {
                if (blnNewlnFlag == true) {
                    endPos = pos;
                } else {
                    blnNewlnFlag = true;
                    pos = -1;
                }
            }
        } while (pos == -1);
        contentType = fileData.substring(startPos + 14, endPos);
        //out.print("BOUNDARY:" + boundary+" start:"+startPos);  
        //loop for extracting actual file data (any type of file)  
        BOF = count + 1;
        System.out.println("END:" + file.substring(count));
        System.out.println("boundary" + boundary.length());

        do {
            copyByte(dataBytes, line, count, 1);	//read 1 byte at a time  
            count += 1;
            s = new String(line, 0, 1);
            fileData = fileData + s;
            pos = fileData.indexOf(boundary, startPos);	//check for end of file data i.e boundry value	
        } while (pos == -1);
        EOF = count - boundary.length();
        System.out.println("EOF" + EOF + "BOF" + BOF);
        //file data extracted  

        if (!filename.equals("")) {
            out.println("<br><br>0. Local File Name = " + strLocalFileName);
            out.println("<br><br>1. filename = " + filename);
            System.out.println("filename:" + filename);
            out.println("<br>2. contentType = " + contentType);
            out.println("<br>3. startPos = " + BOF);
            out.println("<br>4. endPos = " + EOF);
            out.println("<br>5. boundary = " + boundary);

            //create destination path & save file there  
            String appPath = application.getRealPath("/");
            System.out.println("=============" + appPath);
            out.println("<br>appPath : " + appPath);
            String destFolder = appPath + "uploadedFile/";	//change this as required  
            filename = destFolder + filename;
            FileOutputStream fileOut = new FileOutputStream(filename);
            fileOut.write(dataBytes, BOF, (EOF - BOF));
            fileOut.flush();
            fileOut.close();
            out.println("<br>File saved as >> " + filename);
             out.println("count: "+ (count + boundary.length()));
            //file saved at destination  
            out.println("<br>File data : <br><br>**************************<br>" + (new String(dataBytes, startPos, (endPos - startPos))) + "<br><br>**************************");
        } else {
            out.println("Filename Kosong!");
            
        }
       
    } else {
        out.println("Error in uploading ");
    }

%>  