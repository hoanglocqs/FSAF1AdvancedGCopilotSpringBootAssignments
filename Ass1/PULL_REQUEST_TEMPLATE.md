1. In Task 1 (Refactoring placeOrder), what specific "code smell" did the original method have? How does the "Extract Method" refactoring improve the code's maintainability?
    - Method placeOrder gốc đang có nhiều logic trong 1 method, làm quá nhiều chức năng
    - Method placeOrder mới tách ra nhiều method, mỗi method có 1 nhiệm vụ cụ thể, code dễ đọc, có thẻ test riêng từng method
2. In Task 2 (Debugging), which of the three bugs was the most difficult for you to create a prompt for? Why do you think that is? What makes a good prompt for debugging?
    Bug C về missing @Transactional là khó nhất vì:
        - Không có lỗi compile
        - Chỉ xuất hiện lỗi runtime và trong một số trường hợp đặc biệt, ảnh hưởng nghiêm trọng đến tính nhất quán của dữ liệu
        - Khó tái hiện trong môi trường test
3. In Task 3 (Security Analysis), besides the issues Copilot found, can you think of one other potential security risk in a typical e-commerce application?
    Tôi nghĩ là Cross-Site Scripting (XSS).