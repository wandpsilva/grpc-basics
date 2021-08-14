package com.github.wandpsilva.grpc.blogservice.client;

import com.proto.blog.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class BlogClient {

    public static void main(String[] args) {
        //starting and preparing the channel
        System.out.println("gRPC client for Blog");
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        //run the rpc calls
        BlogClient main = new BlogClient();
        main.run(channel);

        //shutdown the server
        System.out.println("shutting down gRPC client");
        channel.shutdown();
    }

    private void run(ManagedChannel channel) {
        BlogServiceGrpc.BlogServiceBlockingStub blogClient = BlogServiceGrpc.newBlockingStub(channel);

        Blog blog = Blog.newBuilder()
                .setTitle("New blog")
                .setContent("Hello world! this is my first blog")
                .setAuthorId("Wander")
                .build();

        //calling the service createBlog
        CreateBlogResponse response = blogClient.createBlog(CreateBlogRequest.newBuilder()
                .setBlog(blog)
                .build());

        System.out.println("Blog created with success, id: " + response.getBlog().getId());
        System.out.println(response.toString());

        String blodId = response.getBlog().getId();

        //calling the service readBlog
        ReadBlogResponse readBlogResponse = blogClient.readBlog(ReadBlogRequest.newBuilder()
                .setBlogId(blodId)
                .build());

        System.out.println(readBlogResponse.toString());

        //calling the service updateBlog
        Blog newBlog = Blog.newBuilder()
                .setId(blodId)
                .setAuthorId("Wander P Silva")
                .setTitle("Updating")
                .setContent("Testing my update service")
                .build();
        UpdateBlogResponse updateResponse = blogClient.updateBlog(UpdateBlogRequest.newBuilder()
                .setBlog(newBlog)
                .build());

        System.out.println("Updated blog!");
        System.out.println(updateResponse.toString());

        //calling the service deleteBlog
        System.out.println("Deleting blog!");
        DeleteBlogResponse deleteResponse = blogClient.deleteBlog(DeleteBlogRequest.newBuilder().setBlogId(blodId).build());
        System.out.println("Blog deleted!");

        //trying to read the blog with the id that has been deleted, expected a not found exception
        ReadBlogResponse readBlogResponseAfterDeletion = blogClient.readBlog(ReadBlogRequest.newBuilder()
                .setBlogId(blodId)
                .build());

        //calling the service listBlog
        blogClient.listBlog(ListBlogRequest.newBuilder().build())
                .forEachRemaining(resp -> System.out.println(resp.getBlog().toString()));
    }
}
