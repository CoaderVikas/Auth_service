package com.vikas.auth.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.multipart.MultipartFile;

/**
 * Class      : UserUtils
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Jun 20, 2026
 * Version    : 1.0
 */

public class UserUtils {

	//public static final String CUSTOM_PERMANENT_DIR = "C:/tenants/images/";
	//public static final String VERIFICATION_PERMANENT_DIR = "E:/verification/images/";
	
	public static String storeImage(MultipartFile file, String userid) {
		Path permanentPath=null;
		try {
			// Extract file extension or default to .jpg
			String originalFilename = file.getOriginalFilename();
			String extension = originalFilename != null && originalFilename.contains(".")
					? originalFilename.substring(originalFilename.lastIndexOf("."))
					: ".jpg";
			String uniqueFileName = userid + "_profile" + extension;

			// --- LOCATION 1: OS / Java Temp Directory Area ---
			String tempDir = System.getProperty("java.io.tmpdir");
			permanentPath = Paths.get(tempDir, uniqueFileName);
			Files.copy(file.getInputStream(), permanentPath, StandardCopyOption.REPLACE_EXISTING);
			System.out.println("Image saved in Temp Area: " + permanentPath.toString());

			// --- LOCATION 2: User Custom Permanent Location ---
			/*File customDir = new File(CUSTOM_PERMANENT_DIR);
			if (!customDir.exists()) {
				customDir.mkdirs(); // Creates directory if it doesn't exist
			}
			permanentPath = Paths.get(CUSTOM_PERMANENT_DIR, uniqueFileName);
			Files.copy(file.getInputStream(), permanentPath, StandardCopyOption.REPLACE_EXISTING);
			System.out.println("Image saved in Custom Area: " + permanentPath.toString());*/

			// Returning the permanent path to save in the Database
			return permanentPath.toString();

		} catch (IOException e) {
			throw new AuthenticationServiceException("Failed to store tenant image: " + e.getMessage());
		}
	}
	
	/**
	 * Stores a verification document (ID proof / ownership proof) and returns the
	 * stored path.
	 *
	 * @param file   uploaded document
	 * @param userId owner/user id
	 * @param docTag short tag to distinguish files e.g. "id_proof",
	 *               "ownership_proof"
	 * @return stored absolute path to persist in DB
	 */
	public static String storeVerificationDocument(MultipartFile file, String userId, String docTag) {
		if (file == null || file.isEmpty()) {
			throw new AuthenticationServiceException("Document file is missing or empty");
		}
		Path permanentPath;
		try {
			String originalFilename = file.getOriginalFilename();
			String extension = originalFilename != null && originalFilename.contains(".")
					? originalFilename.substring(originalFilename.lastIndexOf("."))
					: ".jpg";

			String uniqueFileName = userId + "_" + docTag + "_" + System.currentTimeMillis() + extension;

			String tempDir = System.getProperty("java.io.tmpdir");
			permanentPath = Paths.get(tempDir, uniqueFileName);
			Files.copy(file.getInputStream(), permanentPath, StandardCopyOption.REPLACE_EXISTING);

			return permanentPath.toString();
		} catch (IOException e) {
			throw new AuthenticationServiceException("Failed to store verification document: " + e.getMessage());
		}
	}
	
}
