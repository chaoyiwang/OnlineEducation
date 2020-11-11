#### Project Description: 

This project is a B2C online education system, similar to Udemy, which can be divided into a front-end website and a back-end management platform. It provides online courses, especially for students and training institutions. Teachers can publish and manage courses according to their needs, and provide corresponding course content to students as well. Students can watch videos online and download learning materials anytime and anywhere in order to meet their needs of self-study.

#### Tech Stack: 

Back End: Spring Boot + Spring Cloud + MyBatis-Plus + HttpClient + MySQL + Maven + EasyExcel + Nginx

Others: Redis + Aliyun OSS + Aliyun VoD + Aliyun SMS

#### Deliverables:

In this project, microservices architecture is built with Spring Boot and Spring Cloud in the back-end and the front-end is separately developed from the back-end.  In addition, MyBatis-Plus acts as the persistence layer and Swagger generates API documents. Besides, Alibaba Cloud OSS and VoD are also applied to meet the needs of file storage and playing videos online. 

**Back-end Management**

Both the banner management module and teacher management module have realized CRUD operations and pagination queries. Meanwhile, banner is displayed on the homepage through the Swiper plugin, and the homepage data is also stored in the Redis cache.

In the course classification module, Aliyun OSS is applied to upload and store the files. EasyExcel is also applied to read the course classification data in the uploaded excel file and add them into the database so that the course classification list can be displayed in a tree structure. 

The course management module has realized CRUD operations and conditional pagination queries  for all chapters and videos and realized the function of uploading and playing videos online by using Aliyun VoD. Feign is also used to invocate other services, which implements the function of deleting chapters and videos in cloud storage at the same time. 

In the statistical analysis module, the data can be collected based on different needs, such as the number of new registered user or video views, and displayed through ECharts according to the time span.

**Front-end User**

In the part of new user registration, verification code achieved by Aliyun SMS is applied to verify user's mobile phone number. 

In the part of user login and logout, JWT is generated for traditional username/password logins, otherwise OAuth 2.0 is used for login by scanning QR Code. 

In order management, users can scan the WeChat QR Code generated to complete the payment process after the order is generated and HttpClient is also used to check the payment status on a regular basis.
