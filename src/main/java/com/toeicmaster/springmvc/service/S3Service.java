package com.toeicmaster.springmvc.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
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
		Region usWest2 = Region.getRegion(Regions.US_WEST_2);
		s3.setRegion(usWest2);
	}

	public java.io.InputStream getFile(String bucketName, String key) {
		S3Object object = s3.getObject(new GetObjectRequest(bucketName, key));
		return object.getObjectContent();
	}

	public String uploadS3(String path, String form, String value, int id) {
		String bucketName = "toeic-master-website";
		String exer_folder1 = "exercises";
		String exer_folder2_1 = "audio";
		String exer_folder2_2 = "photo";
		
		String exam_folder1 = "examination";
		String exam_folder2_1 = "audio";
		String exam_folder2_2 = "photo";

		
		java.io.File f = new java.io.File(path);

		String key = f.getName();
		// upload private
		// s3.putObject(new PutObjectRequest(bucketName, key,f));
		// upload public
		/*s3.putObject(
				new PutObjectRequest(bucketName, f.getName(), f).withCannedAcl(CannedAccessControlList.PublicRead));*/
		
//		String object_key = new StringBuilder().append(exer_folder1 +"/").append(f.getName()).toString();
//		s3.putObject(new PutObjectRequest(bucketName,object_key,f).withCannedAcl(CannedAccessControlList.PublicRead));
		
		String URL = "";
		if(form == "exer") {
			if(value == "audio") {
				String object_key = new StringBuilder().append(exer_folder1 +"/").append(exer_folder2_1 +"/").append("audio_"+id+".mp3").toString();
				s3.putObject(new PutObjectRequest(bucketName,object_key,f).withCannedAcl(CannedAccessControlList.PublicRead));
				
				URL = "https://s3-us-west-2.amazonaws.com/" + bucketName + "/" + object_key;
				return URL;
			} else {
				String object_key = new StringBuilder().append(exer_folder1 +"/").append(exer_folder2_2 +"/").append("photo_"+id+".jpg").toString();
				s3.putObject(new PutObjectRequest(bucketName,object_key,f).withCannedAcl(CannedAccessControlList.PublicRead));
				
				URL = "https://s3-us-west-2.amazonaws.com/" + bucketName + "/" + object_key;
				return URL;
			}
		} else if(form == "exam") {
			if(value == "audio") {
				String object_key = new StringBuilder().append(exam_folder1 +"/").append(exam_folder2_1 +"/").append("audio_"+id+".mp3").toString();
				s3.putObject(new PutObjectRequest(bucketName,object_key,f).withCannedAcl(CannedAccessControlList.PublicRead));
				
				URL = "https://s3-us-west-2.amazonaws.com/" + bucketName + "/" + object_key;
				return URL;
			} else {
				String object_key = new StringBuilder().append(exam_folder1 +"/").append(exam_folder2_2 +"/").append("photo_"+id+".jpg").toString();
				s3.putObject(new PutObjectRequest(bucketName,object_key,f).withCannedAcl(CannedAccessControlList.PublicRead));
				
				URL = "https://s3-us-west-2.amazonaws.com/" + bucketName + "/" + object_key;
				return URL;
			}
		}
		return URL;
	}

}
