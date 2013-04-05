/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tubes3;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
/**
 *
 * @author Yulianti Oenang
 */
@WebServlet(name = "profile", urlPatterns = {"/profile"})
public class profile extends HttpServlet {
    public String username,fullname,email,avatar;
    public String birthday;
    public List<String> tugasSelesai=new ArrayList<String>();
    public List<String> tugasBelumSelesai=new ArrayList<String>();
    public String password;
    //,tugasBelumSelesai;
    ResultSet rs;
    public profile() throws SQLException {
        Tubes3Connection tu = new Tubes3Connection();
        Connection connection = tu.getConnection();
        String user = "yuli";
        username=user;
        
        String queryUser = "SELECT * FROM pengguna WHERE username ='" + user+"'";
        String queryTaskSelesai ="SELECT name FROM tugas WHERE username='"+user+"' and stat=1";
        String queryTaskBelumSelesai ="SELECT name FROM tugas WHERE username='"+user+"' and stat=0";
        rs=tu.coba(connection,queryUser);
        if (rs.next())
        {
        fullname=rs.getString("fullname");
        email=rs.getString("email");
        avatar=rs.getString("avatar");
        birthday=rs.getString("birthday");
        password=rs.getString("password");
        }
        else {
            fullname = "Gak ada";
        }
        rs=tu.coba(connection,queryTaskSelesai);
        while(rs.next())
        {
            tugasSelesai.add(rs.getString("name"));
        }
        rs=tu.coba(connection,queryTaskBelumSelesai);
        while(rs.next())
        {
            tugasBelumSelesai.add(rs.getString("name"));
        }
        
    }
    
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet profile</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet profile at " + request.getContextPath () + " lljkjl</h1>");
            out.println("</body>");
            out.println("</html>");
        } finally {            
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    
     private static final long serialVersionUID = 1L;
     
    // location to store file uploaded
    private static final String UPLOAD_DIRECTORY = "avatar";
 
    // upload settings
    private static final int MEMORY_THRESHOLD   = 1024 * 1024 * 3;  // 3MB
    private static final int MAX_FILE_SIZE      = 1024 * 1024 * 40; // 40MB
    private static final int MAX_REQUEST_SIZE   = 1024 * 1024 * 50; // 50MB
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String fullname,birthday,password,confpassword,avatar;
        fullname=request.getParameter("namalengkap");
        birthday=request.getParameter("birthday");
        password=request.getParameter("password");
        confpassword=request.getParameter("confirmedpass");
        avatar=request.getParameter("avatar");
        String[] parts=this.avatar.split("/");
        
        if(this.fullname==fullname && this.birthday==birthday && this.password==password && (parts[1]==avatar || avatar=="") )
		{
			//$_SESSION['flag']=1;
		}
		else
		{
			//$_SESSION['flag']=0;
			this.fullname=fullname;
			this.birthday=birthday;
			this.password=password;
			if(avatar=="")
			{
				String query="UPDATE pengguna SET fullname='"+fullname+"',birthday='"+birthday+"', password='"+password+"' WHERE username='"+this.username+"'";
			}
			else
			{
                             // checks if the request actually contains upload file
                                if (!ServletFileUpload.isMultipartContent(request)) {
                                    // if not, we stop here
                                    PrintWriter writer = response.getWriter();
                                    writer.println("Error: Form must has enctype=multipart/form-data.");
                                    writer.flush();
                                    return;
                                }

                                // configures upload settings
                                DiskFileItemFactory factory = new DiskFileItemFactory();
                                // sets memory threshold - beyond which files are stored in disk 
                                factory.setSizeThreshold(MEMORY_THRESHOLD);
                                // sets temporary location to store files
                                factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

                                ServletFileUpload upload = new ServletFileUpload(factory);

                                // sets maximum size of upload file
                                upload.setFileSizeMax(MAX_FILE_SIZE);

                                // sets maximum size of request (include file + form data)
                                upload.setSizeMax(MAX_REQUEST_SIZE);

                                // constructs the directory path to store upload file
                                // this path is relative to application's directory
                                String uploadPath = getServletContext().getRealPath("")
                                        + File.separator + UPLOAD_DIRECTORY;

                                // creates the directory if it does not exist
                                File uploadDir = new File(uploadPath);
                                if (!uploadDir.exists()) {
                                    uploadDir.mkdir();
                                }

                                try {
                                    // parses the request's content to extract file data
                                    @SuppressWarnings("unchecked")
                                    List<FileItem> formItems = upload.parseRequest(request);

                                    if (formItems != null && formItems.size() > 0) {
                                        // iterates over form's fields
                                        for (FileItem item : formItems) {
                                            // processes only fields that are not form fields
                                            if (!item.isFormField()) {
                                                String fileName = new File(item.getName()).getName();
                                                String filePath = uploadPath + File.separator + fileName;
                                                File storeFile = new File(filePath);

                                                // saves the file on disk
                                                item.write(storeFile);
                                                request.setAttribute("message",
                                                    "Upload has been done successfully!");
                                            }
                                        }
                                    }
                                } catch (Exception ex) {
                                    request.setAttribute("message",
                                            "There was an error: " + ex.getMessage());
                                }
                                // redirects client to message page
                                getServletContext().getRequestDispatcher("/profile.jsp").forward(
                                        request, response);
                            
                            /*
				try{
                                    List<FileItem> items=new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
                                     String fieldname = items.getFieldName();
                                    String fieldvalue = items.getString();
                                }catch(FileUploadException e){
                                    throw new ServletException("cannot parse multipart requist", e);
                                }
                                String target_path = "avatar/";

				target_path = target_path+ request.getParameter("avatar").get

				if(move_uploaded_file($_FILES['avatar']['tmp_name'], $target_path)) {
					$_SESSION['uploadsukses']=1;
				} else{
					$_SESSION['uploadsukses']=0;
				}
				$profile->avatar="avatar/".$avatar;
				$query='UPDATE pengguna SET fullname=\''.$namalengkap.'\',birthday=\''.$birthday.'\', password=\''.$password.'\', avatar=\''.$profile->avatar.'\' WHERE username=\''.$_SESSION['bananauser'].'\'';
			
                                 */
                        }
			
			/*
			$hasil=mysql_query($query);
			
			if($hasil)
			{
				$_SESSION['flagquery']=1;
			}
			else
			{
				$_SESSION['flagquery']=0;
			}
		}
		
		header('location:profile.php');
        processRequest(request, response);
        */
    }
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}