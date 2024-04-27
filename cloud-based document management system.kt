implementation("software.amazon.awssdk:s3-kotlin:2.17.93")

import kotlinx.coroutines.runBlocking
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.File

fun uploadDocument(bucketName: String, key: String, file: File) {
    val s3Client = S3Client.builder()
        .region(Region.US_EAST_1)
        .credentialsProvider(DefaultCredentialsProvider.create())
        .build()

    val request = PutObjectRequest.builder()
        .bucket(bucketName)
        .key(key)
        .build()

    val response = s3Client.putObject(request, file.toPath())
    println("Document uploaded successfully. ETag: ${response.eTag()}")
    s3Client.close()
}

fun main() {
    val bucketName = "your-bucket-name"
    val key = "example/document.txt"
    val file = File("path/to/document.txt")

    uploadDocument(bucketName, key, file)
}
