import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import java.sql.*;

@WebServlet("/upload")
@MultipartConfig
public class FileUploadServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		InputStream filecontent = null;
		PrintWriter writer = response.getWriter();
		try {
			// This class represents a part or form item that was received
			// within a multipart/form-data POST request.
			Part part = request.getPart("file"); // interface in
													// javax.servlet.http
			String fileName = getFileName(part);// user method
			String type = part.getContentType();
			final int size = (int) part.getSize();

			filecontent = part.getInputStream(); // Gets the content of this
													// part as an InputStream

			saveIntoProjectDirectory(fileName, filecontent, writer);

			if (filecontent != null) {
				filecontent.close();
			}
			if (writer != null) {
				writer.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
			writer.println("ERROR: " + e.getMessage());
		}
	}

	private void saveIntoProjectDirectory(String fileName,
			InputStream filecontent, PrintWriter writer) throws Exception {

		ServletContext servletContext = getServletContext();
		String contextPath = servletContext.getRealPath(File.separator);

		OutputStream os = new FileOutputStream(contextPath + "/uploads/"
				+ fileName);

		IOUtils.copy(filecontent, os);
		os.close();

		writer.println("New file " + fileName + " uploaded successfully "
				+ contextPath);
	}

	private String getFileName(final Part part) {
		for (String content : part.getHeader("content-disposition").split(";")) {
			if (content.trim().startsWith("filename")) {
				return content.substring(content.indexOf('=') + 1).trim()
						.replace("\"", "");
			}
		}
		return null;
	}
}
