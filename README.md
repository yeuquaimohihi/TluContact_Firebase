Mô tả chi tiết bài toán ứng dụng TLUContact sử dụng Android Kotlin và CSDL Firebase. Mục tiêu là tạo ra một bản mô tả rõ ràng, giúp cho việc phát triển ứng dụng sau này được dễ dàng hơn.
1. Tên ứng dụng: TLUContact (Danh bạ Đại học Thủy Lợi)
2. Nền tảng: Android (sử dụng ngôn ngữ Kotlin)
3. Cơ sở dữ liệu: Firebase (cụ thể là Cloud Firestore và có thể sử dụng thêm Firebase Authentication)
4. Mô tả tổng quan:
TLUContact là một ứng dụng di động được thiết kế để cung cấp một danh bạ điện tử toàn diện cho Đại học Thủy Lợi (TLU). Ứng dụng cho phép người dùng (bao gồm cán bộ, giảng viên và sinh viên) truy cập thông tin liên lạc của các đơn vị, cán bộ giảng viên (CBGV) và sinh viên trong trường. Ứng dụng có hệ thống phân quyền dựa trên loại tài khoản (CBGV hoặc sinh viên) để đảm bảo tính bảo mật và phù hợp với nhu cầu của từng đối tượng người dùng.
5. Yêu cầu chức năng chi tiết:
5.1. Đăng ký và Đăng nhập:
•	Đăng ký:
o	Người dùng phải sử dụng email do trường cung cấp để đăng ký tài khoản.
o	Kiểm tra định dạng email: 
	CBGV: @tlu.edu.vn
	Sinh viên: @e.tlu.edu.vn
o	Ứng dụng sẽ sử dụng Firebase Authentication để quản lý tài khoản và xác thực email. 
	Có thể gửi email xác minh để đảm bảo email hợp lệ.
o	Thông tin cần thiết khi đăng ký: 
	Email (từ trường)
	Mật khẩu (do người dùng tự đặt, cần có quy định về độ mạnh mật khẩu)
	Xác nhận mật khẩu
	Họ và tên
	(Có thể yêu cầu thêm Mã cán bộ/Mã sinh viên ngay trong lúc đăng ký, hoặc bổ sung sau khi đăng nhập vào phần cập nhật thông tin cá nhân). Điều này giúp liên kết tài khoản người dùng với thông tin đã có trong cơ sở dữ liệu.
o	Sau khi đăng ký thành công, loại tài khoản (CBGV hoặc sinh viên) sẽ được xác định dựa trên định dạng email và lưu trữ trong cơ sở dữ liệu.
•	Đăng nhập:
o	Người dùng đăng nhập bằng email và mật khẩu đã đăng ký.
o	Sử dụng Firebase Authentication để xác thực.
o	Nếu đăng nhập thành công, chuyển đến giao diện chính của ứng dụng.
o	Có thể có tùy chọn "Quên mật khẩu" (sử dụng Firebase Authentication để reset password).
5.2. Giao diện chính (sau khi đăng nhập):
•	Hiển thị 3 tùy chọn chính: 
o	Danh bạ Đơn vị: Hiển thị danh sách các đơn vị trong trường.
o	Danh bạ CBGV: Hiển thị danh sách cán bộ, giảng viên.
o	Danh bạ Sinh viên: Hiển thị danh sách sinh viên.
•	Hiển thị thông tin cơ bản của người dùng đã đăng nhập (ví dụ: tên, ảnh đại diện (nếu có)).
5.3. Danh bạ Đơn vị:
•	Hiển thị: 
o	Danh sách các đơn vị (ví dụ: Khoa, Phòng, Ban, Trung tâm...) được hiển thị dưới dạng danh sách (ListView hoặc RecyclerView).
o	Mỗi đơn vị hiển thị: 
	Tên đơn vị
	Logo (nếu có)
	Mã đơn vị (nếu cần)
o	Có thanh tìm kiếm để tìm kiếm đơn vị theo tên hoặc mã.
o	Có tùy chọn sắp xếp (ví dụ: theo tên A-Z, Z-A).
o	Có thể có bộ lọc (filter) để lọc theo loại đơn vị (ví dụ: chỉ hiển thị các Khoa).
•	Chi tiết đơn vị: 
o	Khi nhấp vào một đơn vị, hiển thị thông tin chi tiết của đơn vị đó: 
	Mã đơn vị
	Tên đơn vị
	Địa chỉ
	Logo
	Điện thoại
	Email
	Fax (nếu có)
	Đơn vị cha/cấp trên (nếu có) - hiển thị dạng liên kết, khi nhấp vào sẽ chuyển đến trang chi tiết của đơn vị cha.
	Danh sách đơn vị con (nếu có) - hiển thị dạng danh sách, mỗi đơn vị con có thể nhấp vào để xem chi tiết.
•	Phân quyền 
o	CBGV: xem được hết tất cả thông tin
o	Sinh viên: xem được hết tất cả thông tin
5.4. Danh bạ CBGV:
•	Hiển thị: 
o	Danh sách CBGV hiển thị dưới dạng danh sách.
o	Mỗi CBGV hiển thị: 
	Họ và tên
	Ảnh đại diện (nếu có)
	Chức vụ
	Tên đơn vị trực thuộc (dạng liên kết, nhấp vào để xem chi tiết đơn vị).
o	Có thanh tìm kiếm để tìm kiếm theo tên, chức vụ, hoặc mã cán bộ.
o	Có tùy chọn sắp xếp (ví dụ: theo tên, theo chức vụ, theo đơn vị).
o	Có bộ lọc (ví dụ: lọc theo đơn vị).
•	Chi tiết CBGV: 
o	Khi nhấp vào một CBGV, hiển thị thông tin chi tiết: 
	Mã cán bộ
	Họ và tên
	Chức vụ
	Điện thoại
	Email
	Ảnh đại diện
	Đơn vị trực thuộc (dạng liên kết, nhấp vào để xem chi tiết đơn vị).
•	Phân quyền 
o	CBGV: xem được hết tất cả thông tin
o	Sinh viên: Không được xem danh bạ này.
5.5. Danh bạ Sinh viên:
•	Hiển thị: 
o	Danh sách sinh viên hiển thị dưới dạng danh sách.
o	Mỗi sinh viên hiển thị: 
	Họ và tên
	Ảnh đại diện (nếu có)
	Mã sinh viên
	Thông tin lớp (hoặc đơn vị) mà sinh viên đó trực thuộc.
•	Có thanh tìm kiếm để tìm theo tên, mã sinh viên, hoặc lớp.
•	Có bộ lọc và sắp xếp.
•	Chi tiết sinh viên: 
o	Khi nhấp vào một sinh viên, hiển thị thông tin chi tiết: 
	Mã sinh viên
	Họ và tên
	Ảnh đại diện
	Số điện thoại
	Email
	Địa chỉ nơi ở
	Lớp (hoặc đơn vị trực thuộc).
•	Phân quyền: 
o	CBGV: Xem được hết tất cả thông tin.
o	Sinh viên: Chỉ xem được danh sách sinh viên cùng lớp (cùng đơn vị trực thuộc cấp thấp nhất).
5.6. Cập nhật thông tin cá nhân:
•	Mỗi người dùng (CBGV và sinh viên) có thể truy cập vào trang "Thông tin cá nhân" của mình.
•	Cho phép người dùng cập nhật các thông tin sau: 
o	Ảnh đại diện
o	Số điện thoại
o	Địa chỉ (đối với sinh viên là địa chỉ nơi ở)
o	Các thông tin khác có thể thay đổi (tùy theo yêu cầu thực tế).
•	Không cho phép người dùng thay đổi email (vì email dùng để xác định tài khoản và phân quyền).
•	Nhấn nút "Lưu" để cập nhật thông tin lên cơ sở dữ liệu.
6. Cấu trúc cơ sở dữ liệu (Firebase Cloud Firestore):
Gợi ý cấu trúc:
•	Collection users:
o	Document ID: (UID từ Firebase Authentication)
o	email: (String)
o	role: (String) - "CBGV" hoặc "SV"
o	displayName: (String) - Họ và tên
o	photoURL: (String) - Đường dẫn đến ảnh đại diện (có thể lưu trữ trên Firebase Storage)
o	phoneNumber: (String)
o	... (các thông tin cá nhân khác)
•	Collection units: (Đơn vị)
o	Document ID: (Tự động tạo hoặc dùng mã đơn vị)
o	code: (String) - Mã đơn vị
o	name: (String) - Tên đơn vị
o	address: (String)
o	logoURL: (String)
o	phone: (String)
o	email: (String)
o	fax: (String)
o	parentUnit: (DocumentReference) - Tham chiếu đến đơn vị cha (nếu có). Nếu là đơn vị cấp cao nhất thì trường này có thể null hoặc không tồn tại.
o	type: (String) - Loại đơn vị (ví dụ: "Khoa", "Phòng", "Trung tâm")... (có thể dùng cho việc lọc)
•	Collection staff: (CBGV)
o	Document ID: (Tự động tạo hoặc dùng mã cán bộ)
o	staffId: (String) - Mã cán bộ
o	fullName: (String)
o	position: (String) - Chức vụ
o	phone: (String)
o	email: (String)
o	photoURL: (String)
o	unit: (DocumentReference) - Tham chiếu đến đơn vị trực thuộc.
o	userId: (String) - (UID từ Firebase Authentication) để link tới thông tin user
•	Collection students: (Sinh viên)
o	Document ID: (Tự động tạo hoặc dùng mã sinh viên)
o	studentId: (String) - Mã sinh viên
o	fullName: (String)
o	photoURL: (String)
o	phone: (String)
o	email: (String)
o	address: (String)
o	class: (String) - Thông tin lớp hoặc đơn vị trực thuộc cấp thấp.
o	userId: (String)- (UID từ Firebase Authentication) để link tới thông tin user
7. Lưu ý quan trọng:
•	Bảo mật: 
o	Sử dụng Firebase Authentication để quản lý xác thực người dùng.
o	Sử dụng Firebase Security Rules để kiểm soát quyền truy cập dữ liệu (đảm bảo sinh viên chỉ xem được thông tin của lớp mình, CBGV có quyền xem rộng hơn).
•	Hiệu suất: 
o	Sử dụng RecyclerView để hiển thị danh sách lớn một cách hiệu quả.
o	Tối ưu hóa truy vấn dữ liệu Firebase (ví dụ: sử dụng limit(), orderBy(), where(), startAt(), endAt() để lấy dữ liệu cần thiết).
o	Sử dụng Cloud Functions (nếu cần) để xử lý logic phức tạp phía server.
•	Trải nghiệm người dùng: 
o	Thiết kế giao diện trực quan, dễ sử dụng.
o	Cung cấp phản hồi rõ ràng cho người dùng (ví dụ: hiển thị thông báo loading khi đang tải dữ liệu).
•	Kiểm thử: Cần kiểm thử kỹ càng các chức năng, đặc biệt là phần phân quyền và bảo mật.
•	Mở rộng: Thiết kế database và code theo hướng module, dễ mở rộng khi cần thiết.

