package com.github.wandpsilva.grpc.blogservice.server;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.proto.blog.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.lang.annotation.Documented;

import static com.mongodb.client.model.Filters.eq;

public class BlogServiceImpl extends BlogServiceGrpc.BlogServiceImplBase {

    private MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
    private MongoDatabase database = mongoClient.getDatabase("mydb");
    private MongoCollection<Document> collection = database.getCollection("blog");

    @Override
    public void createBlog(CreateBlogRequest request, StreamObserver<CreateBlogResponse> responseObserver) {
        System.out.println("Received create blog request");

        Blog blog = request.getBlog();
        Document doc = new Document("author_id", blog.getAuthorId())
                .append("title", blog.getTitle())
                .append("content", blog.getContent());

        System.out.println("Inserting blog......");
        //insert document in mongoDB
        collection.insertOne(doc);
        //mongoDB generated id
        String id = doc.getObjectId("_id").toString();
        System.out.println("Blog created: " + id);

        CreateBlogResponse response = CreateBlogResponse.newBuilder()
                .setBlog(blog.toBuilder().setId(id)).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void readBlog(ReadBlogRequest request, StreamObserver<ReadBlogResponse> responseObserver) {
        System.out.println("Received read blog request");

        String blogId = request.getBlogId();

        System.out.println("Searching for blog");
        Document result = null;
        try {
            result = collection.find(eq("_id", new ObjectId(blogId))).first();
        } catch (Exception ex) {
            System.out.println("Blog not found");
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("The blog was not found!")
                            .augmentDescription(ex.getLocalizedMessage())
                            .asRuntimeException()
            );
        }

        if (request == null) {
            System.out.println("Blog not found");
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("The blog was not found!")
                            .asRuntimeException()
            );
        } else {
            System.out.println("Blog found, sending response");
            Blog blog = documentToBlog(result);
            responseObserver.onNext(ReadBlogResponse.newBuilder().setBlog(blog).build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void updateBlog(UpdateBlogRequest request, StreamObserver<UpdateBlogResponse> responseObserver) {
        System.out.println("Received update blog request");

        Blog blog = request.getBlog();

        String blogId = request.getBlog().getId();

        System.out.println("Searching for blog to update it");
        Document result = null;
        try {
            result = collection.find(eq("_id", new ObjectId(blogId))).first();
        } catch (Exception ex) {
            System.out.println("Blog not found");
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("The blog was not found!")
                            .augmentDescription(ex.getLocalizedMessage())
                            .asRuntimeException()
            );
        }

        if (request == null) {
            System.out.println("Blog not found");
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("The blog was not found!")
                            .asRuntimeException()
            );
        } else {
            Document replacement = new Document("author_id", blog.getAuthorId())
                    .append("_id", new ObjectId(blogId))
                    .append("title", blog.getTitle())
                    .append("content", blog.getContent());

            System.out.println("Replacing data in database...");

            collection.replaceOne(eq("_id", result.getObjectId("_id")), replacement);

            System.out.println("Replaced! sending response");
            responseObserver.onNext(UpdateBlogResponse.newBuilder().setBlog(documentToBlog(replacement)).build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void deleteBlog(DeleteBlogRequest request, StreamObserver<DeleteBlogResponse> responseObserver) {
        System.out.println("Received a delete blog request");

        String blogId = request.getBlogId();
        DeleteResult result = null;
        try {
            result = collection.deleteOne(eq("_id", new ObjectId(blogId)));
        } catch (Exception ex) {
            System.out.println("Blog not found");
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("The blog was not found!")
                            .augmentDescription(ex.getLocalizedMessage())
                            .asRuntimeException()
            );
        }

        if (result.getDeletedCount() == 0) {
            System.out.println("Blog not found");
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("The blog was not found!")
                            .asRuntimeException()
            );
        } else {
            System.out.println("Blog successful deleted");
            responseObserver.onNext(DeleteBlogResponse.newBuilder()
                    .setBlogId(blogId)
                    .build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void listBlog(ListBlogRequest request, StreamObserver<ListBlogResponse> responseObserver) {
        System.out.println("Received list blog request");

        collection.find().iterator().forEachRemaining(document -> responseObserver.onNext(ListBlogResponse.newBuilder()
                .setBlog(documentToBlog(document))
                .build()));

        responseObserver.onCompleted();
    }

    private Blog documentToBlog(final Document result) {
        return Blog.newBuilder()
                .setAuthorId(result.getString("author_id"))
                .setTitle(result.getString("title"))
                .setContent(result.getString("content"))
                .setId(result.getObjectId("_id").toString())
                .build();
    }
}
