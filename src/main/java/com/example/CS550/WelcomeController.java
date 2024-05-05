package com.example.CS550;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.springframework.ui.Model;
import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.PreparedStatement;
import com.example.ValidatingFormInput.ValidateLoginDetails;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;

@Controller
public class WelcomeController implements WebMvcConfigurer  {
	private Connection connection;
	 static String username;
	static String password;

	

   
	
	@Value("${oracle.url}")
    private String oracleUrl;


    @GetMapping("/")
	public String index() {
		return "index";
	}

	@GetMapping("/login")
	public String login(){

		return "login";
	}
	


	@PostMapping("/login")
public String loginForm(
        @Valid ValidateLoginDetails validateLoginDetails,
        BindingResult bindingResult,
        @RequestParam String username,
        @RequestParam String password,
        Model model) {

   

  
        // Establish a connection to the Oracle database
        try  {
			this.connection = DriverManager.getConnection(oracleUrl, username, password);
            // Your logic here, e.g., perform additional authentication checks
			
            // Redirect to the home page on successful login
            return "redirect:/home";
        }
     catch (SQLException e) {
        // Log the exception
        // e.printStackTrace();
        model.addAttribute("errorMessage", "Invalid Login Details or Connection.");
        // Redirect to the dashboard with the error message
        
        return "login";
    }
}

@GetMapping("/home")
public String home(){

	return "home";
}


@PostMapping("/home")
public String homeForm(@RequestParam String path, Model model) {
	
	
    try (FileReader reader = new FileReader(path);
         BufferedReader bufferedReader = new BufferedReader(reader);
		 Statement statement = this.connection.createStatement();
			
         
		 
		 ) {
		
        System.out.println("Executing commands at: " + path);
        String line;
        int lineNumber = 0;
        StringBuilder builder = new StringBuilder();
        
       

        while ((line = bufferedReader.readLine()) != null) {
            lineNumber += 1;
            line = line.trim();
			int count = 0;
            if (line.isEmpty() || line.startsWith("--")) {
                continue;
            }

            builder.append(line);

            if (line.endsWith(";")) {
                builder.setLength(builder.length() - 1); // Remove the trailing semicolon

                try {
                    statement.executeUpdate(builder.toString());
					System.out.println(++count + " Command successfully executed: " +
                                 builder.substring(0, Math.min(builder.length(), 15)) + "...");
                         builder.setLength(0);
					

                } catch (SQLException e) {
                    // Rollback the transaction if an error occurs
                  
                    System.err.println("At line " + lineNumber + ": " + e.getMessage() + "\n");
                    return "redirect:/errorPages"; // Redirect to an error page or handle the error accordingly
                }

               
            }
        }

        

    } catch (Exception e) {
        // e.printStackTrace();
		// System.err.println("Error occurred: " + e.getMessage());
        model.addAttribute("errorMessage", "Invalid or missing Path.");
        // Redirect to the dashboard with the error message
        // Handle the exception and redirect to an error page or handle the error accordingly
        return "home";
		
    }

    return "redirect:/dashboard";
}


@GetMapping("/dashboard")
public String dashboard(Model model) {
    try {
        Statement statement = this.connection.createStatement();

        ResultSet resultSet = statement.executeQuery("SELECT * FROM publications");
		List<Map<String, Object>> resultList = new ArrayList<>();
		
		
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

       

        while (resultSet.next()) {
            Map<String, Object> row = new HashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                Object value = resultSet.getObject(i);
                row.put(columnName, value);
            }
            resultList.add(row);
        }
		
        model.addAttribute("resultList", resultList);
		
        return "dashboard";
    } catch (SQLException e) {
        // e.printStackTrace();
        // Handle the exception and redirect to an error page or handle the error accordingly
        return "dashboard";
    }
}

@GetMapping("/authors")
public String Authors(Model model){
    try {
        Statement statement = this.connection.createStatement();

        ResultSet resultSet = statement.executeQuery("SELECT * FROM authors");
		List<Map<String, Object>> resultList = new ArrayList<>();
		
		
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

         while (resultSet.next()) {
            Map<String, Object> row = new HashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                Object value = resultSet.getObject(i);
                row.put(columnName, value);
            }
            resultList.add(row);
        }
		
        model.addAttribute("resultList", resultList);
		
        return "authors";
    } catch (SQLException e) {
        e.printStackTrace();
        // Handle the exception and redirect to an error page or handle the error accordingly
        return "redirect:/errorPage";
    }

}
    @GetMapping("/searchPublication")    
    public String SearchPublication(){
        
        return "publication";
    }

    
    @PostMapping("/searchPublication")
    public String SearchById(@RequestParam Integer id, Model model){

         try {
    		
    		String query = "SELECT * FROM publications WHERE PUBLICATIONID = ?";
    		PreparedStatement preparedStatement = this.connection.prepareStatement(query);
    		preparedStatement.setInt(1, id);
    		ResultSet resultSet = preparedStatement.executeQuery();
            
            
            List<Map<String, Object>> resultList = new ArrayList<>();
		
		
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            if (resultSet != null) {
            	
            	
            		
            while (resultSet.next()) {
            Map<String, Object> row = new HashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                Object value = resultSet.getObject(i);
                row.put(columnName, value);
              
                            
            }
            
        
            resultList.add(row);
        }model.addAttribute("resultList", resultList);

            }
            
            
        } catch (SQLException e) {
            model.addAttribute("errorMessage", "Invalid ID");
        
            return "publication";
            
        }
        return "publication";

    }

    @GetMapping("/edit")
    public String Edit(){

        return "edit";

    }
    
    @PostMapping("/edit")
    public String EditUrl(@RequestParam Integer id, @RequestParam String csv, Model model){

        try (BufferedReader reader = new BufferedReader(new FileReader(csv))) {
            String line;
                                   
            while ((line = reader.readLine()) != null) {
                String[] nextLine = line.split(",");
                
                int csvPublicationID = Integer.parseInt(nextLine[0]);
                String  newUrl = nextLine[1];
                
                if (id == csvPublicationID ) {
                    nextLine[1] = newUrl;
                    String query = "UPDATE publications SET URL = ? WHERE PUBLICATIONID = ?";
 
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, newUrl);
                    preparedStatement.setInt(2, id);
 
                    int rowsUpdated = preparedStatement.executeUpdate();
                    
                    System.out.println(rowsUpdated + " rows updated.");
                    
                    String newQuery = "SELECT * from publications WHERE PUBLICATIONID =?";
                    PreparedStatement preparedStatementNew = connection.prepareStatement(newQuery);
                     preparedStatementNew.setInt(1,id);
                     
                     ResultSet resultSet = preparedStatementNew.executeQuery();
 
                      List<Map<String, Object>> resultList = new ArrayList<>();
		
		
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            if (resultSet != null) {
            	
            	
            		
            while (resultSet.next()) {
            Map<String, Object> row = new HashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                Object value = resultSet.getObject(i);
                row.put(columnName, value);
              
                            
            }
            
        
            resultList.add(row);
        }model.addAttribute("resultList", resultList);

            }
                    
                    
                    
                    
                }
                
            }
        } 
        catch (Exception e) {
            System.out.println( "Sorry you entered an invalid file path, try again");
           
             }
             return "edit";
    }

    @GetMapping("/logout")
    public String logout(){

       
        return "redirect:/login";
    }

}
