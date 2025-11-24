package edu.univ.erp.util; // or wherever you place temporary files

import java.util.HashMap;
import java.util.Map;
// Assuming PasswordUtil uses org.mindrot.jbcrypt.BCrypt internally

public class HashGenerator {

    public static void main(String[] args) {
        // Step 1: Define your list of professors and a simple default password
        // Use a structure like Map<Username, PlaintextPassword>
        Map<String, String> professorList = new HashMap<>();

        // Use a consistent, simple default password for all 100 accounts initially
        String defaultPassword = "password123";

        // Populate this map with the usernames of your 100 professors
        professorList.put("A V Subramanyam", defaultPassword);
        professorList.put("Aasim Khan", defaultPassword);
        professorList.put("Abhijit Mitra", defaultPassword);
        professorList.put("Angshul Majumdar", defaultPassword);
        professorList.put("Anmol Srivastava", defaultPassword);
        professorList.put("Anubha Gupta", defaultPassword);
        professorList.put("Anuj Grover", defaultPassword);
        professorList.put("Anuradha Sharma", defaultPassword);
        professorList.put("Arani Bhattacharya", defaultPassword);
        professorList.put("Arjun Ray", defaultPassword);
        professorList.put("Arun Balaji Buduru", defaultPassword);
        professorList.put("Ashish Kumar Pandey", defaultPassword);
        professorList.put("Bapi Chatterjee", defaultPassword);
        professorList.put("Chanekar Prasad Vilas", defaultPassword);
        professorList.put("Debajyoti Bera", defaultPassword);
        professorList.put("Debarka Sengupta", defaultPassword);
        professorList.put("Debidas Kundu", defaultPassword);
        professorList.put("Debika Banerjee", defaultPassword);
        professorList.put("Deepak Prince", defaultPassword);
        professorList.put("Diptapriyo Majumdar", defaultPassword);
        professorList.put("G.P.S. Raghava", defaultPassword);
        professorList.put("Ganesh Bagler", defaultPassword);
        professorList.put("Gaurav Ahuja", defaultPassword);
        professorList.put("Gaurav Arora", defaultPassword);
        professorList.put("Gautam Shroff", defaultPassword);
        professorList.put("Gayatri Nair", defaultPassword);
        professorList.put("J. V. Meenakshi", defaultPassword);
        professorList.put("Jainendra Shukla", defaultPassword);
        professorList.put("Jaspreet Kaur Dhanjal", defaultPassword);
        professorList.put("Kalpana Shankhwar", defaultPassword);
        professorList.put("Kaushik Kalyanaraman", defaultPassword);
        professorList.put("Kiriti Kanjilal", defaultPassword);
        professorList.put("Manohar Kumar", defaultPassword);
        professorList.put("Manuj Mukherjee", defaultPassword);
        professorList.put("Md. Shad Akhtar", defaultPassword);
        professorList.put("Monika Arora", defaultPassword);
        professorList.put("Mrinmoy Chakrabarty", defaultPassword);
        professorList.put("Mukesh Mohania", defaultPassword);
        professorList.put("Mukulika Maity", defaultPassword);
        professorList.put("N. Arul Murugan", defaultPassword);
        professorList.put("Nabanita Ray", defaultPassword);
        professorList.put("Nikhil Gupta", defaultPassword);
        professorList.put("Nishad Patnaik", defaultPassword);
        professorList.put("Ojaswa Sharma", defaultPassword);
        professorList.put("Paro Mishra", defaultPassword);
        professorList.put("Piyus Kedia", defaultPassword);
        professorList.put("Pragma Kar", defaultPassword);
        professorList.put("Pragya Kosta", defaultPassword);
        professorList.put("Prahllad Deb", defaultPassword);
        professorList.put("Praveen Priyadarshi", defaultPassword);
        professorList.put("Pravesh Biyani", defaultPassword);
        professorList.put("Pushpendra Singh", defaultPassword);
        professorList.put("Rajiv Raman", defaultPassword);
        professorList.put("Rajiv Ratn Shah", defaultPassword);
        professorList.put("Ram Krishna Ghosh", defaultPassword);
        professorList.put("Ranjan Bose", defaultPassword);
        professorList.put("Ranjitha Prasad", defaultPassword);
        professorList.put("Ravi Anand", defaultPassword);
        professorList.put("Richa Gupta", defaultPassword);
        professorList.put("Rinku Shah", defaultPassword);
        professorList.put("Ruhi Sonal", defaultPassword);
        professorList.put("Sachchidanand Prasad", defaultPassword);
        professorList.put("Saket Anand", defaultPassword);
        professorList.put("Sambuddho", defaultPassword);
        professorList.put("Sanat K Biswas", defaultPassword);
        professorList.put("Sanjit Krishnan Kaul", defaultPassword);
        professorList.put("Sankha S Basu", defaultPassword);
        professorList.put("Sarthok Sircar", defaultPassword);
        professorList.put("Satish Kumar Pandey", defaultPassword);
        professorList.put("Sayak Bhattacharya", defaultPassword);
        professorList.put("Sayan Basu Roy", defaultPassword);
        professorList.put("Shamik Sarkar", defaultPassword);
        professorList.put("Shobha Sundar Ram", defaultPassword);
        professorList.put("Smriti Singh", defaultPassword);
        professorList.put("Sneh Saurabh", defaultPassword);
        professorList.put("Sneha Chaubey", defaultPassword);
        professorList.put("Soibam Haripriya", defaultPassword);
        professorList.put("Sonal Keshwani", defaultPassword);
        professorList.put("Sonia Baloni Ray", defaultPassword);
        professorList.put("Souvik Dutta", defaultPassword);
        professorList.put("Sriram K", defaultPassword);
        professorList.put("Subhashree Mohapatra", defaultPassword);
        professorList.put("Sujay Deb", defaultPassword);
        professorList.put("Sumit J Darak", defaultPassword);
        professorList.put("Supratim Shit", defaultPassword);
        professorList.put("Syamantak Das", defaultPassword);
        professorList.put("Tanmoy Kundu", defaultPassword);
        professorList.put("Tarini Shankar Ghosh", defaultPassword);
        professorList.put("Tavpritesh Sethi", defaultPassword);
        professorList.put("V. Raghava Mutharaju", defaultPassword);
        professorList.put("Venkata Ratnadeep Suri", defaultPassword);
        professorList.put("Vibhor Kumar", defaultPassword);
        professorList.put("Vikram Goyal", defaultPassword);
        professorList.put("Vinayak Abrol", defaultPassword);
        professorList.put("Vivek Bohara", defaultPassword);
        professorList.put("Vivek Kumar", defaultPassword);
        professorList.put("Pankaj Jalote", defaultPassword);




        System.out.println("--- Generated BCrypt Hashes for SQL INSERT ---");

        // Step 2: Iterate and generate the hash for each user
        for (Map.Entry<String, String> entry : professorList.entrySet()) {
            String username = entry.getKey();
            String plaintext = entry.getValue();

            // Assume PasswordUtil.hashPassword(plaintext) exists and uses BCrypt
            String hashedPassword = PasswordUtil.hashPassword(plaintext);

            // Step 3: Print the SQL INSERT statement
            System.out.printf("('\"%s\"', 'Instructor', '\"%s\"', 'Active'),%n", username, hashedPassword);
        }

        System.out.println("--- END OF HASH GENERATION ---");
    }
}