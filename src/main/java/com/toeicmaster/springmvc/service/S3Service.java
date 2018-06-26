package com.toeicmaster.springmvc.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import com.amazonaws.services.s3.transfer.TransferManager;

public class S3Service {
	private AmazonS3 s3 = null;
	// private AWSCredentials credentials = null;
	private BasicAWSCredentials credentials = null;

	public S3Service() {
		try {
			credentials = new BasicAWSCredentials("AKIAJQYCNZTHZYISLT5Q", "8GCc14Z3j8RJt8rhLXs85/LNl5N1Dx/vwvceXm7C");
			/*
			 * AmazonS3 s3Client = AmazonS3ClientBuilder.standard() .withCredentials(new
			 * AWSStaticCredentialsProvider(credentials)) .build();
			 */
			// credentials = new ProfileCredentialsProvider().getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException("Cannot load the credentials from the credential profiles file. "
					+ "Please make sure that your credentials file is at the correct "
					+ "location (~/.aws/credentials), and is in valid format.", e);
		}

		s3 = new AmazonS3Client(credentials);
		// s3 = AmazonS3ClientBuilder.standard()
		// .withCredentials(new AWSStaticCredentialsProvider(credentials))
		// .build();
		Region AP_SOUTHEAST_1 = Region.getRegion(Regions.AP_SOUTHEAST_1);
		s3.setRegion(AP_SOUTHEAST_1);
	}

	public java.io.InputStream getFile(String bucketName, String key) {
		S3Object object = s3.getObject(new GetObjectRequest(bucketName, key));
		return object.getObjectContent();
	}

	public String uploadS3(MultipartFile mulfile, String form, String value, int id) {
		String bucketName = "toeic-master-web";
		String exer_folder1 = "exercise";
		String exer_folder2_1 = "audio";
		String exer_folder2_2 = "photo";
		
		String exam_folder1 = "examination";
		String exam_folder2_1 = "audio";
		String exam_folder2_2 = "photo";

		
//		java.io.File f = new java.io.File(path);

//		String key = f.getName();
		String key = mulfile.getOriginalFilename();
		
		String URL = "";
		
		try {
			File file = convertMultiPartToFile(mulfile);
			
			if(form == "exer") {
				if(value == "audio") {
					String object_key = new StringBuilder().append(exer_folder1 +"/").append(exer_folder2_1 +"/").append("audio_"+id+".mp3").toString();
					s3.putObject(new PutObjectRequest(bucketName,object_key,file).withCannedAcl(CannedAccessControlList.PublicRead));
					
					URL = "https://s3-ap-southeast-1.amazonaws.com/" + bucketName + "/" + object_key;
					return URL;
				} else {
					String object_key = new StringBuilder().append(exer_folder1 +"/").append(exer_folder2_2 +"/").append("photo_"+id+".jpg").toString();
					s3.putObject(new PutObjectRequest(bucketName,object_key,file).withCannedAcl(CannedAccessControlList.PublicRead));
					
					URL = "https://s3-ap-southeast-1.amazonaws.com/" + bucketName + "/" + object_key;
					return URL;
				}
			} else if(form == "exam") {
				if(value == "audio") {
					String object_key = new StringBuilder().append(exam_folder1 +"/").append(exam_folder2_1 +"/").append("audio_"+id+".mp3").toString();
					s3.putObject(new PutObjectRequest(bucketName,object_key,file).withCannedAcl(CannedAccessControlList.PublicRead));
					
					URL = "https://s3-ap-southeast-1.amazonaws.com/" + bucketName + "/" + object_key;
					return URL;
				} else {
					String object_key = new StringBuilder().append(exam_folder1 +"/").append(exam_folder2_2 +"/").append("photo_"+id+".jpg").toString();
					s3.putObject(new PutObjectRequest(bucketName,object_key,file).withCannedAcl(CannedAccessControlList.PublicRead));
					
					URL = "https://s3-ap-southeast-1.amazonaws.com/" + bucketName + "/" + object_key;
					return URL;
				}
			}
			
			file.delete();
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
		
		return URL;
	}
	public void deleteS3(String type, int id, String audio_url, String photo_url) {
		String bucketName = "toeic-master-web";
		String exer_folder1 = "exercise";
		String exer_folder2_1 = "audio";
		String exer_folder2_2 = "photo";
		
		String exam_folder1 = "examination";
		String exam_folder2_1 = "audio";
		String exam_folder2_2 = "photo";
		String object_key = "";
			
		if(type == "exer") {
			
			if(audio_url != null) {
				object_key = new StringBuilder().append(exer_folder1 +"/").append(exer_folder2_1 +"/").append("audio_"+id+".mp3").toString();
				s3.deleteObject(new DeleteObjectRequest(bucketName, object_key));
			}
			if (photo_url != null){
				object_key = new StringBuilder().append(exer_folder1 +"/").append(exer_folder2_2 +"/").append("photo_"+id+".jpg").toString();
				s3.deleteObject(new DeleteObjectRequest(bucketName, object_key));							
			}
		} else if(type == "exam") {	
			if(audio_url != null) {
				object_key = new StringBuilder().append(exam_folder1 +"/").append(exam_folder2_1 +"/").append("audio_"+id+".mp3").toString();
				s3.deleteObject(new DeleteObjectRequest(bucketName, object_key));		
			}
			if(photo_url == null) {
				object_key = new StringBuilder().append(exam_folder1 +"/").append(exam_folder2_2 +"/").append("photo_"+id+".jpg").toString();
				s3.deleteObject(new DeleteObjectRequest(bucketName, object_key));										
			}
		}		
	}
	private File convertMultiPartToFile(MultipartFile file) throws IOException {
	    File convFile = new File(file.getOriginalFilename());
	    FileOutputStream fos = new FileOutputStream(convFile);
	    fos.write(file.getBytes());
	    fos.close();
	    return convFile;
	}
}
