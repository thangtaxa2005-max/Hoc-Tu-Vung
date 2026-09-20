# Nhớ Từ — SPEC

> Nguồn sự thật duy nhất của project. Khi thiết kế đổi: sửa file này TRƯỚC, sửa code SAU.

## 1. Mô tả

Nhớ Từ là app Android giúp người học tiếng Anh (luyện TOEIC) **nhớ từ chủ động** và **luyện nghe**.
Khác với app chọn đáp án A/B/C/D, app này bắt người dùng **tự gõ** từ tiếng Anh:

- Nhìn nghĩa tiếng Việt → gõ từ.
- Nhìn câu bị khuyết chỗ trống (kiểu TOEIC Part 5) → gõ từ.
- Nghe phát âm → gõ lại từ/câu vừa nghe.

Từ được ôn theo lịch lặp lại ngắt quãng (spaced repetition). Toàn bộ chạy offline, dữ liệu lưu trên máy.

## 2. Stack

| Hạng mục | Chọn |
|---|---|
| Ngôn ngữ | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Kiến trúc | MVVM (ViewModel + StateFlow), Repository |
| Database | Room |
| Cài đặt | DataStore (Preferences) |
| Điều hướng | Navigation Compose |
| Thông báo nhắc ôn | WorkManager |
| Phát âm | `android.speech.tts.TextToSpeech` (không cần mạng/API) |
| DI | Thủ công (ViewModel factory), không dùng Hilt |
| minSdk | 26 |

Không dùng: Retrofit, Firebase, đăng nhập, server.

## 3. Màn hình và route

| Route | Màn hình | Nội dung |
|---|---|---|
| `home` | Trang chủ | Số từ cần ôn hôm nay, nút chọn chế độ ôn, số từ đã thuộc |
| `review/{mode}` | Ôn tập | `mode` = `recall` \| `cloze` \| `listening` |
| `add` | Thêm từ | Nhập 1 thẻ |
| `addBulk` | Thêm hàng loạt | Dán nhiều dòng, mỗi dòng 1 thẻ |
| `library` | Kho từ | Danh sách, tìm kiếm, xóa |
| `card/{id}` | Chi tiết/sửa thẻ | Sửa từ, nghĩa, câu ví dụ; xem lịch sử ôn |
| `stats` | Thống kê | Số từ theo mức độ nhớ, từ hay sai nhất, số lượt ôn theo ngày |
| `settings` | Cài đặt | Giờ nhắc, số thẻ mới tối đa mỗi ngày, tốc độ phát âm |

Bottom bar: Trang chủ, Kho từ, Thống kê, Cài đặt.

## 4. Database (Room)

### `CardEntity` (bảng `cards`)

| Field | Kiểu | Ghi chú |
|---|---|---|
| `id` | Long, PK, autoGenerate | |
| `word` | String | Từ/cụm từ tiếng Anh, unique (không phân biệt hoa thường) |
| `meaningVi` | String | Nghĩa tiếng Việt |
| `exampleSentence` | String? | Câu ví dụ chứa `word`, dùng cho chế độ cloze và listening |
| `createdAt` | Long | epoch millis |
| `dueDay` | Long | epoch day của lần ôn tiếp theo |
| `intervalDays` | Int | mặc định 0 |
| `easeFactor` | Double | mặc định 2.5 |
| `repetitions` | Int | mặc định 0 |
| `lapses` | Int | mặc định 0 (số lần sai sau khi đã thuộc) |

### `ReviewLogEntity` (bảng `review_logs`)

| Field | Kiểu | Ghi chú |
|---|---|---|
| `id` | Long, PK, autoGenerate | |
| `cardId` | Long, FK → `cards.id`, ON DELETE CASCADE | |
| `mode` | String | `recall` \| `cloze` \| `listening` |
| `reviewedAt` | Long | epoch millis |
| `quality` | Int | 0–5, xem mục 5 |
| `hintsUsed` | Int | 0–3 |
| `correct` | Boolean | |

Cài đặt (DataStore): `reminderHour`, `reminderMinute`, `reminderEnabled`, `newCardsPerDay`, `ttsSpeed`.

## 5. Thuật toán lặp lại (SM-2 rút gọn)

Đặt trong `domain/srs/Sm2Scheduler.kt`, là **Kotlin thuần, không phụ thuộc Android**, có unit test.

**Chấm quality:**

| Kết quả | quality |
|---|---|
| Đúng, không dùng gợi ý | 5 |
| Đúng, dùng gợi ý nấc 1 | 4 |
| Đúng, dùng gợi ý nấc 2 | 3 |
| Sai, hoặc phải hiện cả từ (nấc 3) | 1 |

**Cập nhật thẻ:**

- Nếu `quality < 3`: `repetitions = 0`, `intervalDays = 1`, `lapses += 1` (nếu thẻ từng thuộc).
- Ngược lại: `repetitions += 1`; `intervalDays` = 1 nếu là lần 1, 6 nếu là lần 2, còn lại `round(intervalDays * easeFactor)`.
- `easeFactor = max(1.3, easeFactor + 0.1 - (5 - q) * (0.08 + (5 - q) * 0.02))`.
- `dueDay = hôm nay + intervalDays`.

**Gợi ý 3 nấc:** (1) hiện chữ cái đầu; (2) hiện khoảng nửa số chữ cái; (3) hiện cả từ (tính là sai).

**Kiểm tra đáp án** (`AnswerChecker`): bỏ khoảng trắng đầu/cuối, không phân biệt hoa thường, gộp nhiều khoảng trắng liền nhau. Phải khớp hoàn toàn (bản đầu chưa chấp nhận sai chính tả).

## 6. Ba chế độ ôn

1. **recall:** hiện `meaningVi` → gõ `word`.
2. **cloze:** lấy `exampleSentence`, thay `word` bằng `_____` (`ClozeBuilder`) → gõ `word`. Chỉ dùng với thẻ có câu ví dụ.
3. **listening:** TTS đọc `word` (hoặc `exampleSentence` nếu có) → gõ lại. Có nút nghe lại (không giới hạn) và nút nghe chậm. Không hiện chữ trước khi trả lời.

Mỗi phiên: lấy các thẻ `dueDay <= hôm nay`, cộng tối đa `newCardsPerDay` thẻ mới (`repetitions = 0`). Sai thì thẻ được xếp lại cuối phiên để làm lại một lần nữa.

## 7. Định dạng thêm hàng loạt

Mỗi dòng: `word | nghĩa tiếng Việt | câu ví dụ (không bắt buộc)`

```
supervisor | người giám sát | The supervisor approved the budget.
resign | từ chức
```

Dòng sai định dạng thì bỏ qua và báo số dòng lỗi. Từ đã tồn tại thì bỏ qua.

## 8. Cấu trúc package

```
com.example.nhotu
├── data
│   ├── local        (AppDatabase, CardDao, ReviewLogDao, entity)
│   ├── settings     (SettingsRepository, DataStore)
│   └── repository   (CardRepository)
├── domain
│   ├── srs          (Sm2Scheduler)
│   ├── AnswerChecker.kt
│   ├── ClozeBuilder.kt
│   └── BulkParser.kt
├── tts              (TtsManager)
├── notification     (ReminderWorker, ReminderScheduler)
└── ui
    ├── navigation   (NavGraph, Routes)
    ├── theme
    ├── home
    ├── review
    ├── add
    ├── library
    ├── stats
    └── settings
```

## 9. Danh sách task

Mỗi task nhỏ, có tiêu chí xong. Làm theo thứ tự. Sau mỗi task: build, chạy thử, đọc code, commit Git.

| # | Task | Xong khi |
|---|---|---|
| 1 | Tạo project, theme, NavGraph, bottom bar với màn hình rỗng | App chạy, chuyển qua lại giữa các màn hình |
| 2 | `CardEntity`, `CardDao`, `AppDatabase` | Test insert/query chạy qua |
| 3 | `Sm2Scheduler` + unit test | Test cover: đúng lần 1, lần 2, lần 3, sai, easeFactor không xuống dưới 1.3 |
| 4 | `CardRepository`, màn Thêm từ, màn Kho từ (xem, xóa) | Thêm một từ thì thấy trong Kho từ, tắt mở app vẫn còn |
| 5 | `BulkParser` + màn Thêm hàng loạt | Unit test parser qua; dán 10 dòng thì có 10 thẻ |
| 6 | Màn Ôn tập chế độ `recall` (chưa có gợi ý) | Ôn hết phiên; `dueDay` và `easeFactor` cập nhật đúng |
| 7 | Gợi ý 3 nấc + chấm quality | Dùng gợi ý thì quality giảm đúng bảng |
| 8 | `ReviewLogEntity` + ghi log mỗi lượt ôn | Mỗi câu trả lời có 1 dòng log |
| 9 | `ClozeBuilder` + chế độ `cloze` | Unit test qua; câu khuyết hiển thị đúng |
| 10 | `TtsManager` + chế độ `listening` | Nghe được, nghe lại, nghe chậm, gõ lại được chấm đúng |
| 11 | Trang chủ (số từ cần ôn, chọn chế độ) | Số hiển thị khớp dữ liệu thật |
| 12 | Màn Thống kê | Biểu đồ/số liệu khớp với log |
| 13 | `ReminderWorker` + màn Cài đặt (DataStore) | Nhận được thông báo đúng giờ đã đặt |
| 14 | Hoàn thiện: màn hình trống, xoay màn hình, xử lý lỗi TTS không có | Không crash khi kho từ trống hoặc thiết bị thiếu TTS |

## 10. Ngoài phạm vi (bản đầu)

Đồng bộ đám mây, đăng nhập, nhận diện giọng nói, tự động dịch/lấy nghĩa, chấp nhận sai chính tả gần đúng, import từ file Anki. Có thể làm sau khi 14 task trên xong.

## 11. Quy tắc khi nhờ AI viết code

- Đọc `SPEC.md` trước khi làm.
- Chỉ làm đúng task được giao.
- Không thêm field, bảng, file hay thư viện ngoài spec.
- Thấy spec thiếu hoặc mâu thuẫn thì hỏi lại, không tự đoán.
