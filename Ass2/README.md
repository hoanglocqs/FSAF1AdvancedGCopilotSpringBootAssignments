1. Explain the trade-offs of caching. When might caching the findProductById result be a bad idea (e.g., if product prices change very frequently)?
 
-Giảm số lượng truy vấn DB khi findProductById, tăng hiệu suất
-request findProductById không cần tính toán lại.
-Sản phẩm có thể không realtime nếu không cache đúng cách.
-Quản lý cache phức tạp, cần reset cache khi có thay đổi ở DB.
-Không phù hợp với dữ liệu thay đổi thường xuyên.

2. What is the difference between a full table scan and an index scan in a database? Why did adding an @Index in Task 2 significantly improve query performance?
Full Table Scan:
-Duyệt qua toàn bộ bảng, từng hàng một, để tìm data thỏa mãn điều kiện rất tốn tài nguyên và chậm trên bảng lớn (hàng chục nghìn bản ghi trở lên).
 
Index Scan:
-DB sử dụng một cấu trúc dữ liệu chỉ mục (index) để tìm bản ghi nhanh hơn nhiều.
-Chỉ truy cập vào các phần cần thiết.
 
-Phương thức searchProducts dùng LIKE hoặc WHERE name = ?, truy vấn theo name cột không index sẽ rất chậm, khi thêm @Index(name = "idx_product_name", columnList = "name"), sẽ tạo chỉ mục SQL thì DB thực hiện index scan thay vì full scan.
-Giúp giảm thời gian truy vấn table product có nhiều dữ liệu.
 
3. Why are default health checks (like disk space, database connection) not always enough? Provide an example of a business-critical dependency for our e-commerce app that would require a custom health indicator.
🔹 Giới hạn của health check mặc định:
Spring Boot Actuator mặc định cung cấp một số health checks như:
-Kiểm tra kết nối DB.
-Kiểm tra dung lượng ổ đĩa còn đủ không.
-Kiểm tra thread pool, memory, etc.

-Trong ứng dụng thương mại điện tử, ta thường dùng các dịch vụ bên ngoài (third-party), nếu những dịch vụ này không hoạt động, dù server vẫn "UP" theo actuator mặc định nhưng hệ thống thực chất vẫn đang gặp sự cố.
 
🔹 Ví dụ: Cổng thanh toán (Payment Gateway)
Ứng dụng phụ thuộc vào cổng thanh toán như PayPal,ZaloPay, Momo...
 
Nếu không thể kết nối tới API của Momo:
-Người dùng không thể thanh toán.
-Đơn hàng không thể hoàn tất.
-Doanh thu bị ảnh hưởng trực tiếp.
 
 