DROP TABLE IF EXISTS `master_vendors`;
CREATE TABLE IF NOT EXISTS `master_vendors` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `vendor_name` varchar(255),
  `vendor_code` varchar(255),
  `description` varchar(255),
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `created_by` VARCHAR(255)
);

DROP TABLE IF EXISTS `master_templates`;
CREATE TABLE IF NOT EXISTS `master_templates` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `master_vendor_id` int,
  `project_type` varchar(255),
  `project_version` varchar(255) COMMENT '1.1.2',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `created_by` VARCHAR(255)
);

DROP TABLE IF EXISTS `master_evaluations`;
CREATE TABLE IF NOT EXISTS `master_evaluations` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `master_template_id` int,
  `code` varchar(255) COMMENT 'ASD',
  `name` varchar(255),
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `created_by` VARCHAR(255)
);

DROP TABLE IF EXISTS `master_exercises`;
CREATE TABLE IF NOT EXISTS `master_exercises` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `master_evaluation_id` int,
  `exercise_type` varchar(255),
  `code` varchar(255) COMMENT 'ASD-P',
  `name` varchar(255) COMMENT 'Basic Green Area',
  `max_score` integer,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `created_by` VARCHAR(255)
);

DROP TABLE IF EXISTS `master_criterias`;
CREATE TABLE IF NOT EXISTS `master_criterias` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `master_exercise_id` int,
  `code` varchar(255) COMMENT 'ASD-P1',
  `description` text,
  `exercise_type` varchar(255),
  `score` float COMMENT 'nullable',
  `additional_notes` text,
  `not_available` bool,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `created_by` VARCHAR(255)
);

DROP TABLE IF EXISTS `master_documents`;
CREATE TABLE IF NOT EXISTS `master_documents` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `master_criteria_id` int,
  `criteria_code` varchar(255) COMMENT 'ASD-P1',
  `name` text,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `created_by` VARCHAR(255)
);

DROP TABLE IF EXISTS `master_criteria_blockers`;
CREATE TABLE IF NOT EXISTS `master_criteria_blockers` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `master_criteria_id` int,
  `blocker_id` int,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `created_by` VARCHAR(255)
);

DROP TABLE IF EXISTS `master_levels`;
CREATE TABLE IF NOT EXISTS `master_levels` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(255),
  `minimum_score` float,
  `percentage` float,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `created_by` VARCHAR(255)
);

DROP TABLE IF EXISTS `project_assessments`;
CREATE TABLE IF NOT EXISTS `project_assessments` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `master_template_id` int,
  `process_instance_id` varchar(255),
  `project_name` varchar(255),
  `possible_score` float,
  `temporary_score` float,
  `potential_score` float,
  `level_id` int,
  `proposed_level_id` int,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `created_by` VARCHAR(255)
);

DROP TABLE IF EXISTS `criteria_scorings`;
CREATE TABLE IF NOT EXISTS `criteria_scorings` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `master_criteria_id` int,
  `project_assessment_id` int,
  `selected` boolean,
  `score` float,
  `potential_score` float,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `created_by` VARCHAR(255)
);

DROP TABLE IF EXISTS `comments`;
CREATE TABLE IF NOT EXISTS `comments` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `criteria_scoring_id` int,
  `user_id` int,
  `role` varchar(255),
  `comment` text,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `created_by` VARCHAR(255)
);

DROP TABLE IF EXISTS `document_files`;
CREATE TABLE IF NOT EXISTS `document_files` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `master_document_id` int,
  `criteria_scoring_id` int,
  `filename` varchar(255),
  `link` varchar(255),
  `uploader_id` int,
  `uploader_name` varchar(255),
  `role` varchar(255),
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `created_by` VARCHAR(255)
);

DROP TABLE IF EXISTS `exercise_assessment`;
CREATE TABLE IF NOT EXISTS `exercise_assessment` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `master_exercise_id` int,
  `project_assessment_id` int,
  `selected` boolean,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `created_by` VARCHAR(255)
);


INSERT INTO `master_vendors`
VALUES
  (1, 'Green Building Council Indonesia', 'GBCI', 'Green Building Council Indonesia', NOW(), NOW(),'system');

INSERT INTO `master_templates`
VALUES
  (1, 1, 'design_recognition', 'GREENSHIP NB 1.2 Exercise ', NOW(), NOW(),'system');


INSERT INTO `master_evaluations`
VALUES
  (1, 1, 'ASD', 'Appropiate Site Development', NOW(), NOW(),'system'),
  (2, 1, 'EEC', 'Energy Effeciency and Conservation', NOW(), NOW(),'system'),
  (3, 1, 'WAC', 'Water Conservation', NOW(), NOW(),'system'),
  (4, 1, 'MRC', 'Material Resource and Cycle', NOW(), NOW(),'system'),
  (5, 1, 'IHC', 'Indoor Health and Comfort', NOW(), NOW(),'system'),
  (6, 1, 'BEM', 'Building Environment Management', NOW(), NOW(),'system');

INSERT INTO `master_exercises`
VALUES
(1, 1, 'prequisite','ASD P','Basic Green Area',null,NOW(),NOW(),'system'),
(2, 1, 'score','ASD 1','Site Selection',2,NOW(),NOW(),'system'),
(3, 1, 'score','ASD 2','Community Accessibility',2,NOW(),NOW(),'system'),
(4, 1, 'score','ASD 3','Public Transportation',2,NOW(),NOW(),'system'),
(5, 1, 'score','ASD 4','Bicycle',2,NOW(),NOW(),'system'),
(6, 1, 'score','ASD 5','Site Landscaping',3,NOW(),NOW(),'system'),
(7, 1, 'score','ASD 6','Micro Climate',3,NOW(),NOW(),'system'),
(8, 1, 'score','ASD 7','Storm Water Management',3,NOW(),NOW(),'system'),
(9, 2, 'prequisite','EEC P1','Electrical Sub Metering',null,NOW(),NOW(),'system'),
(10, 2, 'prequisite','EEC P2','OTTV Calculation',null,NOW(),NOW(),'system'),
(11, 2, 'score','EEC 1','Energy Efficiency Measure',20,NOW(),NOW(),'system'),
(12, 2, 'score','EEC 2','Natural Lighting',4,NOW(),NOW(),'system'),
(13, 2, 'score','EEC 3','Ventilation ',1,NOW(),NOW(),'system'),
(14, 2, 'score','EEC 4','Climate Change Impact',1,NOW(),NOW(),'system'),
(15, 2, 'score','EEC 5','On Site Renewable Energy',5,NOW(),NOW(),'system'),
(16, 3, 'prequisite','WAC P','Water Metering',null,NOW(),NOW(),'system'),
(17, 3, 'score','WAC 1','Water Use Reduction',8,NOW(),NOW(),'system'),
(18, 3, 'score','WAC 2','Water Fixtures',3,NOW(),NOW(),'system'),
(19, 3, 'score','WAC 3','Water Recycling',3,NOW(),NOW(),'system'),
(20, 3, 'score','WAC 4','Alternative Water Resource',2,NOW(),NOW(),'system'),
(21, 3, 'score','WAC 5','Rainwater Harvesting',3,NOW(),NOW(),'system'),
(22, 3, 'score','WAC 6','Water Efficiency Landscaping',2,NOW(),NOW(),'system'),
(23, 4, 'prequisite','MRC P','Fundamental Refrigerant',null,NOW(),NOW(),'system'),
(24, 4, 'score','MRC 1','Building and Material Reuse',0,NOW(),NOW(),'system'),
(25, 4, 'score','MRC 2','Environmentally Processed Product',0,NOW(),NOW(),'system'),
(26, 4, 'score','MRC 3','Non ODS Usage',2,NOW(),NOW(),'system'),
(27, 4, 'score','MRC 4','Certified Wood',0,NOW(),NOW(),'system'),
(28, 4, 'score','MRC 5','Prefab Material',0,NOW(),NOW(),'system'),
(29, 4, 'score','MRC 6','Regional Material',0,NOW(),NOW(),'system'),
(30, 5, 'prequisite','IHC P','Outdoor Air Introduction',null,NOW(),NOW(),'system'),
(31, 5, 'score','IHC 1','CO2 Monitoring',1,NOW(),NOW(),'system'),
(32, 5, 'score','IHC 2','Environmental Tobacco Smoke Control',2,NOW(),NOW(),'system'),
(33, 5, 'score','IHC 3','Chemical Pollutants',0,NOW(),NOW(),'system'),
(34, 5, 'score','IHC 4','Outside View',1,NOW(),NOW(),'system'),
(35, 5, 'score','IHC 5','Visual Comfort',0,NOW(),NOW(),'system'),
(36, 5, 'score','IHC 6','Thermal Comfort',1,NOW(),NOW(),'system'),
(37, 5, 'score','IHC 7','Acoustic Level',0,NOW(),NOW(),'system'),
(38, 6, 'prequisite','BEM P','Basic Waste Management',null,NOW(),NOW(),'system'),
(39, 6, 'score','BEM 1','GP as a Member of The Project Team',1,NOW(),NOW(),'system'),
(40, 6, 'score','BEM 2','Pollution of Construction Activity',0,NOW(),NOW(),'system'),
(41, 6, 'score','BEM 3','Advanced Waste Management',2,NOW(),NOW(),'system'),
(42, 6, 'score','BEM 4','Proper Commissioning',3,NOW(),NOW(),'system'),
(43, 6, 'score','BEM 5','Green Building Data Submission',0,NOW(),NOW(),'system'),
(44, 6, 'score','BEM 6','Fit Out Agreement',0,NOW(),NOW(),'system'),
(45, 6, 'score','BEM 7','Occupant Survey',0,NOW(),NOW(),'system');

INSERT INTO `master_criterias`
VALUES
(1, 1, 'ASD P1','Adanya area lansekap berupa vegetasi (softscape) yang bebas dari struktur bangunan dan struktur sederhana bangunan taman (hardscape) di atas permukaan tanah atau di bawah tanah.                                                            
o Untuk konstruksi baru, luas areanya adalah minimal 10% dari luas total lahan.                                                                                                                                                                             o Untuk  major renovation, luas areanya adalah minimal 50% dari ruang terbuka yang bebas basement dalam tapak.','prequisite',null,'Basement: Ada /Tdk
Luas Area : ………………
Luas RTH Bebas basement : …………………
Luas RTH Total : ……………',false,NOW(),NOW(),'system'),
(2, 1, 'ASD P2','Area ini memiliki vegetasi mengikuti Permendagri No 1 tahun 2007 Pasal 13 (2a) dengan komposisi 50% lahan tertutupi luasan pohon ukuran kecil, ukuran sedang, ukuran besar, perdu setengah pohon, perdu, semak dalam ukuran dewasa dengan jenis tanaman sesuai dengan Permen PU No. 5/PRT/M/2008 mengenai Ruang Terbuka Hijau (RTH) Pasal  2.3.1  tentang Kriteria Vegetasi untuk Pekarangan.','prequisite',null,'Luas Semak :……………..
Luas Tajuk perdu + semak : ………….
Luas Tajuk Pohon : ………………..',false,NOW(),NOW(),'system'),
(3, 2, 'ASD 1-1A','Memilih daerah pembangunan yang dilengkapi minimal delapan dari 12 prasarana sarana kota.
1.Jaringan Jalan.
2. Jaringan penerangan dan Listrik.
3. Jaringan Drainase.
4. STP Kawasan.
5. Sistem Pembuangan Sampah. 
6. Sistem Pemadam Kebakaran. 
7. Jaringan Fiber Optik. 
8. Danau Buatan (Minimal 1% luas area). 
9. Jalur Pejalan Kaki Kawasan. 
10. Jalur Pemipaan Gas. 
11. Jaringan Telepon. 
12. Jaringan Air bersih.','score',1,'Daftar 8 prasana kota:
1. ………………
2.………………
3.………………
4.………………
5.………………
6.………………
7.………………
8.………………',false,NOW(),NOW(),'system'),
(4, 2, 'ASD 1-1B','Memilih daerah pembangunan dengan ketentuan KLB > 3. ','score',1,'',false,NOW(),NOW(),'system'),
(5, 2, 'ASD 1-2','Melakukan revitalisasi dan pembangunan di atas lahan yang bernilai negatif dan tak terpakai karena bekas pembangunan atau dampak negatif pembangunan.','score',1,'Sejarah :………………..',false,NOW(),NOW(),'system'),
(6, 3, 'ASD 2-1','Terdapat minimal tujuh jenis fasilitas umum dalam jarak pencapaian jalan utama sejauh 1500 m dari tapak.
1.Bank.
2.Taman Umum.
3.Parkir Umum (dil luar lahan).
4.Warung/Toko Kelontong.
5.Gedung Serba Guna. 
6. Pos Keamanan/Polisi. 
7.Tempat Ibadah. 
8.Lapangan Olah Raga.
9.Tempat Penitipan Anak.
10.Apotek.
11.Rumah Makan/Kantin.
12.Foto Kopi Umum.
13.Fasilitas Kesehatan.
14.Kantor Pos.
15. Kantor Pemadam Kebakaran.
16. Terminal/Stasiun Transportasi Umum. 
17.Perpustakaan.
18.Kantor Pemerintah.
19.Pasar.','score',1,'Daftar 7 Fasum:
1. ………………
2.………………
3.………………
4.………………
5.………………
6.………………
7.………………',false,NOW(),NOW(),'system'),
(7, 3, 'ASD 2-2','Membuka akses pejalan kaki selain ke jalan utama di luar tapak yang menghubungkan-nya dengan jalan sekunder dan/atau lahan milik orang lain sehingga tersedia akses ke minimal 3 fasilitas umum sejauh 300 m jarak pencapaian pejalan kaki','score',1,'Jalan sekunder/
Daftar 3 Fasum:
1. ………………
2.………………
3.………………

',false,NOW(),NOW(),'system'),
(8, 3, 'ASD 2 - 3','Menyediakan fasilitas/akses yang aman, nyaman, dan bebas dari perpotongan dengan akses kendaraan bermotor untuk menghubungkan secara langsung bangunan dengan bangunan lain, di mana terdapat minimal 3 fasilitas umum dan/atau dengan stasiun transportasi masal','score',2,'Jenis Fasilitas/Akses:
1. Jembatan
2. Terowongan
3. Jalur Pedestrian
4. Pintu tembus

Menyambung dengan:
Stasiun Transportasi massal /
Daftar 3 Fasum:
1. ………………
2.………………
3.………………',false,NOW(),NOW(),'system'),
(9, 3, 'ASD 2 - 4','Membuka lantai dasar gedung sehingga dapat menjadi akses pejalan kaki yang aman dan nyaman selama minimum 10 jam sehari','score',2,'Ya / Tidak',false,NOW(),NOW(),'system'),
(10, 4, 'ASD 3 - 1A','Adanya halte atau stasiun  transportasi umum dalam jangkauan 300 m (walking distance) dari gerbang lokasi bangunan dengan tidak memperhitungkan panjang jembatan penyeberangan dan ramp.','score',1,'Pintu yang digunakan berada di ………….
jarak dari pintu ke halte ….. : ± ……… m',false,NOW(),NOW(),'system'),
(11, 4, 'ASD 3 - 1B','Menyediakan shuttle bus untuk pengguna tetap gedung dengan jumlah unit minimum untuk 10% pengguna tetap gedung','score',1,'Jumlah okupan tetap : …………..
Jumlah seat min. yg diakomodasi : ……………..
Rencana jenis & Jumlah bus : …………..
Office  = Karyawan
mal = Karyawan
RS = Karyawan & Dokter tetap
Hotel = Karyawan
Apartemen = Karyawan
Sekolah = Karyawan + Dosen
Pabrik = Karyawan',false,NOW(),NOW(),'system'),
(12, 4, 'ASD 3 - 2','Menyediakan fasilitas jalur pedestrian di dalam area gedung untuk menuju ke stasiun transportasi umum terdekat yang aman dan nyaman  sesuai dengan Peraturan Menteri PU 30/PRT/M/2006 mengenai Pedoman Teknis Fasilitas dan Aksesibilitas pada Bangunan Gedung dan Lingkungan Lampiran 2B.','score',1,'Fasilitas Pedestrian:
1. Material perkerasan ………. (Permukaan stabil, kuat, Tahan Cuaca, Tekstur halus & tdk licin, tdk ada gundukan).
2. Kemiringan …….. (<1:8)
3. Penerangan jalan ……… (50 - 150 Lux)
4. Drainase ………….. 
5. Lebar ………………. (1,2 - 1,6 M)
6. Kanstin / Low Curb',false,NOW(),NOW(),'system'),
(13, 5, 'ASD 4 - 1','Adanya tempat parkir sepeda yang aman sebanyak 1 unit parkir per 20 pengguna gedung, hingga maksimal 100 unit parkir sepeda.','score',1,'Jumlah okupan tetap : …………..
Jumlah Rack Min : ……………..


Office  = Karyawan
mal = Karyawan
RS = Karyawan & Dokter tetap
Hotel = Karyawan
Apartemen = Karyawan
Sekolah = Karyawan + Dosen
Pabrik = Karyawan',false,NOW(),NOW(),'system'),
(14, 5, 'ASD 4 - 2','Apabila tolok ukur 1 di atas terpenuhi, perlu tersedianya shower sebanyak 1 unit untuk setiap 10 parkir sepeda. ','score',1,'Jumlah Shower Min : ……………
',false,NOW(),NOW(),'system'),
(15, 6, 'ASD 5 -1A','Adanya area lansekap berupa vegetasi (softscape) yang bebas dari bangunan taman (hardscape) yang terletak di atas permukaan tanah seluas minimal 40% luas total lahan. Luas area yang diperhitungkan adalah termasuk yang tersebut di Prasyarat 1, taman di atas basement, roof garden, terrace garden, dan wall garden, sesuai dengan Permen PU No. 5/PRT/M/2008 mengenai Ruang Terbuka Hijau (RTH) Pasal 2.3.1  tentang Kriteria Vegetasi untuk Pekarangan.','score',1,'Luas Area : ………………
Luas RTH  : …………………
Persentase : ……………',false,NOW(),NOW(),'system'),
(16, 6, 'ASD 5 -1B','Bila tolok ukur 1 dipenuhi, setiap penambahan 5% area lansekap dari luas total lahan mendapat 1 poin.','score',1,'Luas Area : ………………
Luas RTH  : …………………
Persentase : ……………',false,NOW(),NOW(),'system'),
(17, 6, 'ASD 5 -2','Penggunaan tanaman yang telah dibudidayakan secara lokal dalam skala provinsi, sebesar 60% luas  tajuk  dewasa terhadap luas areal lansekap pada ASD 5 tolok ukur 1.','score',1,'Luas RTH  : …………………
Luas Tajuk Tanaman Lokal : …………………..
Persentase : ……………',false,NOW(),NOW(),'system'),
(18, 7, 'ASD 6 - 1A','Menggunakan berbagai material untuk menghindari efek heat island pada area atap gedung sehingga nilai albedo (daya refleksi panas matahari) minimum 0,3 sesuai dengan perhitungan','score',1,'Jenis material finishing atap & nilai albedonya  :
1 .……………
2 .……………
3 .……………',false,NOW(),NOW(),'system'),
(19, 7, 'ASD 6 - 1B','Menggunakan green roof sebesar 50% dari luas atap yang tidak digunakan untuk mechanical electrical (ME), dihitung dari luas tajuk','score',1,'',false,NOW(),NOW(),'system'),
(20, 7, 'ASD 6 - 2','Menggunakan berbagai material untuk menghindari efek heat island pada area non-atap sehingga nilai albedo (daya refleksi panas matahari) minimum 0,3 sesuai dengan perhitungan','score',1,'Jenis material RT non hijau  & nilai albedonya :
1 .……………
2 .……………
3 .……………',false,NOW(),NOW(),'system'),
(21, 7, 'ASD 6 - 3A','Desain lansekap berupa vegetasi (softscape) pada sirkulasi utama pejalan kaki menunjukkan adanya pelindung dari panas akibat radiasi matahari.','score',1,'Pelindung panas  :
Kanopi Pohon / Pergola / ………………',false,NOW(),NOW(),'system'),
(22, 7, 'ASD 6 - 3B','Desain lansekap berupa vegetasi (softscape) pada sirkulasi utama pejalan kaki menunjukkan adanya pelindung dari terpaan angin kencang.','score',1,'Pelindung angin dari :
Hedge Semak / Hardscape……….. / ……………
Panjang Formasi Pelindung : …………….',false,NOW(),NOW(),'system'),
(23, 8, 'ASD 7 - 1A','Pengurangan beban volume limpasan air hujan ke jaringan drainase kota dari lokasi bangunan hingga 50 %, yang dihitung menggunakan intensitas curah hujan*.','score',1,'Cara yang digunakan:
1. ………….
2……………
Kapasitas total : …………………',false,NOW(),NOW(),'system'),
(24, 8, 'ASD 7 - 1B','Pengurangan beban volume limpasan air hujan ke jaringan drainase kota dari lokasi bangunan hingga 85 %, yang dihitung menggunakan nilai intensitas curah hujan*.  ','score',2,'Cara yang digunakan:
1. ………….
2……………
Kapasitas total : …………………',false,NOW(),NOW(),'system'),
(25, 8, 'ASD 7 - 2','Menunjukkan adanya upaya penanganan pengurangan beban banjir lingkungan dari luar lokasi bangunan','score',1,'Kapasitas total - 100% beban banjir : …………………',false,NOW(),NOW(),'system'),
(26, 8, 'ASD 7 - 3','Menggunakan teknologi-teknologi yang dapat mengurangi debit limpasan air hujan','score',1,'Teknologi yang digunakan:
1. ………….
2……………',false,NOW(),NOW(),'system'),
(27, 9, 'EEC P1 - 1','Memasang  kWh meter untuk mengukur konsumsi listrik pada setiap kelompok beban dan sistem peralatan, yang meliputi: ','prequisite',null,'BAS : Ya/Tidak
A. Bila menggunakan BAS: Dapat membaca konsumsi energi sistemnyang diminta secara terpisah/ Spesifikasi tidak untuk membaca konsumsi energi yang diminta
B. Bila tidak terbaca :
Letak Submeter : ……………..(deskripsi)
Jenis Kegiatan : ……………….
Jenis ruangan di gedung :
1 .……………
2 .……………
3 .…………...
Sub Meter Exclude (bila perlu): ………………',false,NOW(),NOW(),'system'),
(28, 10, 'EEC P2 - 1','Menghitung dengan cara perhitungan OTTV berdasarkan SNI 03-6389-2011 atau SNI edisi terbaru tentang  Konservasi Energi Selubung Bangunan pada Bangunan Gedung.','prequisite',null,'Nilai OTTV : …………….
 Orientasi  : ……………….
Hadapan Luar fasade terbesar : …………..(orientasi)
WWR : ………….
Double skin ya/Tidak
Jenis kaca :
1.…….. 
2………
Material dinding : 
1.…….. 
2………
Insulasi :
1.…….. 
2………
Material atap :
1.…….. 
2………
 ',false,NOW(),NOW(),'system'),
(29, 11, 'EEC 1-1','Menggunakan Energy modelling software untuk menghitung konsumsi energi di gedung baseline dan gedung designed. Selisih konsumsi energi dari gedung baseline dan designed merupakan penghematan. Untuk setiap penghematan sebesar 2,5%, yang  dimulai dari penurunan energi sebesar 5% dari gedung baseline, mendapat 1 nilai (wajib untuk platinum).','max_score',20,'IKE Desain : ………
IKE Baseline :……….
Jenis Software : …………….
Jenis Sistem AC : ……………
Jenis Chiller (bila sentral) : …………..
Natural Ventilation  : Ada/Tidak
Software Simulasi Pencahayaan : ……………………….
Zonasi Lighting : Ada/Tidak
Sensor Pencahayaan : Ada/Tidak
Bila ada, jenis sensor: ..............
',false,NOW(),NOW(),'system'),
(30, 11, 'EEC 1-2','Menggunakan perhitungan worksheet, setiap penghematan 2% dari selisih antara gedung designed dan baseline mendapat nilai 1 poin. Penghematan mulai dihitung dari penurunan energi sebesar 5% dari gedung baseline. Worksheet yang dimaksud disediakan oleh GBCI.','max_score',20,'IKE Desain : ………
IKE Baseline :……….
Jenis Software : …………….
Jenis Sistem AC : ……………
Jenis Chiller (bila sentral) : …………..
Natural Ventilation  : Ada/Tidak
Software Simulasi Pencahayaan : ……………………….
Zonasi Lighting : Ada/Tidak
Sensor Pencahayaan : Ada/Tidak
Bila ada, jenis sensor: ..............
',false,NOW(),NOW(),'system'),
(31, 11, 'EEC 1-3-1-1','Nilai OTTV sesuai dengan SNI 03-6389-2011 atau SNI edisi terbaru tentang konservasi Energi Selubung Bangunan pada Bangunan Gedung.','score',3,'',false,NOW(),NOW(),'system'),
(32, 11, 'EEC 1-3-1-2','Apabila tolok ukur 1 dipenuhi, penurunan per 2.5W/m2mendapat 1 nilai sampai maksimal 2 nilai. ','score',2,'',false,NOW(),NOW(),'system'),
(33, 11, 'EEC 1-3-2-1','Menggunakan lampu dengan daya pencahayaan sebesar 15%, yang lebih hemat daripada daya pencahayaan yang tercantum dalam SNI 03 6197-2011','score',1,'Jenis lampu yang digunakan:
1 .……………
2 .……………
3 .…………...
Total Lighting Allowance : ……………..W/m2',false,NOW(),NOW(),'system'),
(34, 11, 'EEC 1-3-2-2','Menggunakan 100% ballast frekuensi tinggi (elektronik) untuk ruang kerja','score',1,'Jenis lampu indoor : ……………..',false,NOW(),NOW(),'system'),
(35, 11, 'EEC 1-3-2-3','Zonasi pencahayaan untuk seluruh ruang kerja yang dikaitkan dengan sensor gerak (motion sensor)','score',1,'Software Simulasi Pencahayaan : ……………………….
Letak Sensor : …………………..',false,NOW(),NOW(),'system'),
(36, 11, 'EEC 1-3-2-4','Penempatan tombol lampu dalam jarak pencapaian tangan pada saat buka pintu','score',1,'Deskripsi letak : ……………………………..',false,NOW(),NOW(),'system'),
(37, 11, 'ECC 1-3-3-1','Lift menggunakan traffic management system yang sudah lulus traffic analysis atau menggunakan regenerative drive system','score',1,'Ya / Tidak',false,NOW(),NOW(),'system'),
(38, 11, 'ECC 1-3-3-2','Menggunakan fitur hemat energi pada lift, menggunakan sensor gerak, atau sleep mode pada eskalator','score',1,'',false,NOW(),NOW(),'system'),
(39, 11, 'ECC 1-3-4 COP','Menggunakan peralatan Air Conditioning (AC) dengan COP minimum 10% lebih besar dari standar SNI 03-6390-2011 atau SNI edisi terbaru tentang Konservasi Energi pada Sistem Tata Udara Bangunan Gedung.','score',2,'Jenis Sistem AC : ……………
Jenis Chiller     : …………..
Nilai COP Plant   : ………………',false,NOW(),NOW(),'system'),
(40, 12 , 'EEC 2-1','Penggunaaan cahaya alami secara optimal sehingga minimal 30% luas lantai yang digunakan untuk bekerja mendapatkan intensitas cahaya alami minimal sebesar 300 lux. Perhitungan dapat dilakukan dengan cara manual atau dengan  software. ','score',2,'Software Simulasi Pencahayaan : ……………………….',false,NOW(),NOW(),'system'),
(41, 12, 'EEC 2-2','Jika butir satu dipenuhi lalu ditambah dengan adanya lux sensor untuk otomatisasi pencahayaan buatan apabila intensitas cahaya alami kurang dari 300 lux, didapatkan tambahan nilai 2 poin','score',2,'Letak Sensor : ……………..m dari perimeter
',false,NOW(),NOW(),'system'),
(42, 13, 'ECC 3','Tidak mengkondisikan (tidak memberi AC) ruang WC, tangga, koridor, dan lobi lift, serta melengkapi ruangan tersebut dengan ventilasi alami ataupun mekanik.','score',1,'Ventilasi Alami/Mekanik
Cara Intake:………….
Letak Intake : ……..
Cara Exhaust : …………….
Letak Exhaust : …….',false,NOW(),NOW(),'system'),
(43, 14, 'ECC 4','Menyerahkan perhitungan pengurangan emisi CO2 yang didapatkan dari selisih kebutuhan energi antara design building dan base building dengan menggunakan grid emission factor (konversi antara CO2 dan energi listrik) yang telah ditetapkan dalam Keputusan DNA pada B/277/Dep.III/LH/01/2009','score',1,'Cara perhitungan energi:
 Opsi 1 (Simulasi)/Opsi 2 (Worksheet)',false,NOW(),NOW(),'system'),
(44, 15, 'ECC 5','Menggunakan sumber energi baru dan terbarukan. Setiap 0,5% daya listrik yang dibutuhkan gedung yang dapat dipenuhi oleh sumber energi terbarukan mendapatkan 1 poin (sampai maksimal 5 poin).','max_score',5,'Generator Renewable : …………..
KWP total : …………..',false,NOW(),NOW(),'system'),
(45, 16, 'WAC P','Pemasangan alat meteran air (volume meter) yang ditempatkan di lokasi-lokasi tertentu pada sistem distribusi air, sebagai berikut:','prequisite',null,'Sumber Air Primer : ………………
Ada Fasilitas Recycling dari Grey Water  : ada/tdk
Ada Sumber Air Primer Alternatif :
ada/tdk',false,NOW(),NOW(),'system'),
(46, 16, 'WAC P2','Mengisi worksheet  air standar GBC Indonesia yang telah disediakan','prequisite',null,'Ada perhitungan dengan Water Calculator : ada / tdk',false,NOW(),NOW(),'system'),
(47, 17, 'WAC 1-1','Konsumsi air bersih dengan jumlah tertinggi 80% dari sumber primer tanpa mengurangi jumlah kebutuhan per orang sesuai dengan SNI 03-7065-2005 seperti pada tabel terlampir.','score',1,'Jumlah Okupan : …………….
Kebutuhan air :………………
Persentase : ………………..',false,NOW(),NOW(),'system'),
(48, 17, 'WAC 1-2','Setiap penurunan konsumsi air bersih dari sumber primer sebesar 5% sesuai dengan acuan pada poin 1 akan  mendapatkan nilai 1 dengan dengan nilai maksimum sebesar  7 poin.','max_score',7,'',false,NOW(),NOW(),'system'),
(49, 18, 'WAC 2-1A','Penggunaan water fixture yang sesuai dengan kapasitas buangan di bawah standar maksimum kemampuan alat keluaran air sesuai dengan lampiran, sejumlah minimal 25% dari total pengadaan produk water fixture. ','score',1,'Persentase WF Hemat : …………..
Type WF hemat:
1. .……………
2. .……………
3. .……………
4. .……………
5. .……………',false,NOW(),NOW(),'system'),
(50, 18, 'WAC 2-1B','Penggunaan water fixture yang sesuai dengan kapasitas buangan di bawah standar maksimum kemampuan alat keluaran air sesuai dengan lampiran, sejumlah minimal 50% dari total pengadaan produk water fixture. ','score',2,'',false,NOW(),NOW(),'system'),
(51, 18, 'WAC 2-1C','Penggunaan water fixture yang sesuai dengan kapasitas buangan di bawah standar maksimum kemampuan alat keluaran air sesuai dengan lampiran, sejumlah minimal 75% dari total pengadaan produk water fixture. ','score',3,'',false,NOW(),NOW(),'system'),
(52, 19, 'WAC 3-1A','Penggunaan seluruh air bekas pakai (grey water) yang telah di daur ulang untuk kebutuhan sistem flushing atau cooling tower.','score',2,'Penggunaan air recycling :
1. Flushing
2. Irigasi
3. Cooling Tower',false,NOW(),NOW(),'system'),
(53, 19, 'WAC 3 - 1B','Penggunaan seluruh air bekas pakai (grey water) yang telah didaur ulang untuk kebutuhan sistem flushing dan cooling tower - 3 nilai (hanya untuk water cooled)','score',3,'',false,NOW(),NOW(),'system'),
(54, 20, 'WAC 4-1A','Menggunakan salah satu dari tiga alternatif sebagai berikut: air kondensasi AC, air bekas wudu, atau air hujan.','score',1,'Sumber air Alternative : ………………',false,NOW(),NOW(),'system'),
(55, 20, 'WAC 4-1B','Menggunakan lebih dari satu sumber air dari ketiga alternatif di atas. ','score',2,'Sumber air Alternative : ………………',false,NOW(),NOW(),'system'),
(56, 20, 'WAC 4-1C','Menggunakan teknologi yang memanfaatkan air laut atau air danau atau air sungai untuk keperluan air bersih sebagai sanitasi, irigasi dan kebutuhan lainnya','score',2,'Sumber air Alternative : ………………',false,NOW(),NOW(),'system'),
(57, 21, 'WAC 5-1A','Menyediakan instalasi tangki  penampungan air hujan kapasitas  50% dari jumlah air hujan yang jatuh di atas atap bangunan yang dihitung menggunakan nilai intensitas curah hujan harian rata-rata 10 tahunan setempat.','score',1,'Kapasitas Tangki : ………………….
Persentase penyimpanan : ………….',false,NOW(),NOW(),'system'),
(58, 21, 'WAC 5-1B','Menyediakan Instalasi tangki  penampungan air hujan kapasitas 75%  dari perhitungan di atas.','score',2,'',false,NOW(),NOW(),'system'),
(59, 21, 'WAC 5-1C','Menyediakan Instalasi tangki penyimpanan penampungan air hujan kapasitas 100 % dari perhitungan di atas.','score',3,'',false,NOW(),NOW(),'system'),
(60, 22, 'WAC 6-1','Seluruh air yang digunakan untuk irigasi gedung tidak berasal dari sumber air tanah dan/atau PDAM. ','score',1,'Sumber air irigasi :
1……………….
2……………….',false,NOW(),NOW(),'system'),
(61, 22, 'WAC 6-2','Menerapkan teknologi yang inovatif untuk irigasi yang dapat mengontrol kebutuhan air untuk lansekap yang tepat, sesuai dengan kebutuhan tanaman.','score',1,'Teknologi yang digunakan:
1. ………….
2……………',false,NOW(),NOW(),'system'),
(62, 23, 'MRC P','Tidak menggunakan chloro fluoro carbon (CFC) sebagai refrigeran dan halon sebagai bahan pemadam kebakaran','prequisite',null,'Jenis Refrigeran :………………….
Jenis bahan Fire Fighting: ……………',false,NOW(),NOW(),'system'),
(63, 24, 'MRC 1-1A','Menggunakan kembali semua material bekas, baik dari bangunan lama maupun tempat lain, berupa bahan struktur utama, fasad, plafon, lantai, partisi, kusen, dan dinding, setara minimal 10% dari total biaya material.','score',1,'',true,NOW(),NOW(),'system'),
(64, 24, 'MRC 1-1B','Menggunakan kembali semua material bekas, baik dari bangunan lama maupun tempat lain, berupa bahan struktur utama, fasad, plafon, lantai, partisi, kusen, dan dinding, setara minimal 20% dari total biaya material.','score',2,'',true,NOW(),NOW(),'system'),
(65, 25, 'MRC 2-1','Menggunakan material yang memiliki sertifikat sistem manajemen lingkungan pada proses produksinya minimal bernilai 30% dari total biaya material. Sertifikat dinilai sah bila masih berlaku dalam rentang waktu proses pembelian dalam konstruksi berjalan. ','score',1,'',true,NOW(),NOW(),'system'),
(66, 25, 'MRC 2-2','Menggunakan material yang merupakan hasil proses daur ulang minimal bernilai 5% dari total biaya material. ','score',1,'',true,NOW(),NOW(),'system'),
(67, 25, 'MRC 2-3','Menggunakan material yang bahan baku utamanya berasal dari sumber daya (SD) terbarukan dengan masa panen jangka pendek (<10 tahun) minimal bernilai 2% dari total biaya material.','score',1,'',true,NOW(),NOW(),'system'),
(68, 26, 'MRC 3','Tidak menggunakan bahan perusak ozon pada seluruh sistem pendingin gedung','score',2,'Jenis Refrigeran :………………….',false,NOW(),NOW(),'system'),
(69, 27, 'MRC 4-1','Menggunakan bahan material kayu yang bersertifikat legal sesuai dengan Peraturan Pemerintah tentang asal kayu (seperti faktur angkutan kayu olahan/FAKO, sertifikat perusahaan, dan lain-lain) dan sah terbebas dari perdagangan kayu ilegal sebesar 100% biaya total material kayu','score',1,'',true,NOW(),NOW(),'system'),
(70, 27, 'MRC 4-2','Jika 30% dari butir di atas menggunakan kayu bersertifikasi dari pihak Lembaga Ekolabel Indonesia (LEI) atau Forest Stewardship Council (FSC)','score',1,'',true,NOW(),NOW(),'system'),
(71, 28, 'MRC 5','Desain yang menggunakan material modular atau prafabrikasi (tidak termasuk equipment) sebesar 30% dari total biaya material','score',3,'',true,NOW(),NOW(),'system'),
(72, 29, 'MRC 6-1','Menggunakan material yang lokasi asal bahan baku utama dan pabrikasinya berada di dalam radius 1.000 km dari lokasi proyek minimal bernilai 50% dari total biaya material. ','score',1,'',true,NOW(),NOW(),'system'),
(73, 29, 'MRC 6-2','Menggunakan material yang lokasi asal bahan baku utama dan pabrikasinya berada dalam wilayah Republik Indonesia bernilai minimal 80% dari total biaya material. ','score',1,'',true,NOW(),NOW(),'system'),
(74, 30, 'IHC P','Desain ruangan yang menunjukkan adanya potensi introduksi udara luar minimal sesuai dengan Standar ASHRAE 62.1-2007 atau Standar ASHRAE edisi terbaru.','prequisite',null,'Perhitungan : ada/tidak
Standar acuan yg digunakan : …………………..',false,NOW(),NOW(),'system'),
(75, 31, 'IHC 1','Ruangan dengan kepadatan tinggi, yaitu < 2.3m2 per orang dilengkapi dengan instalasi sensor gas karbon dioksida (CO2) yang memiliki mekanisme untuk mengatur jumlah ventilasi udara luar sehingga konsentrasi C02 di dalam ruangan tidak lebih dari 1.000 ppm, sensor diletakkan 1,5 m di atas lantai dekat return air gril atau return air duct.','score',1,'Ruangan yang dipasang CO2 monitor: 
1. ………………
2.………………
3.………………',false,NOW(),NOW(),'system'),
(76, 32, 'IHC 2','Memasang tanda “Dilarang Merokok di Seluruh Area Gedung” dan tidak menyediakan bangunan/area khusus untuk merokok di dalam gedung. Apabila tersedia, bangunan/area merokok di luar gedung, minimal berada pada jarak  5 m dari pintu masuk, outdoor air intake, dan bukaan jendela.','score',2,'',false,NOW(),NOW(),'system'),
(77, 33, 'IHC 3-1','Menggunakan cat dan coating yang mengandung kadar volatile organic compounds (VOCs) rendah, yang ditandai dengan label/sertifikasi yang diakui GBC Indonesia','score',1,'',true,NOW(),NOW(),'system'),
(78, 33, 'IHC 3-2','Menggunakan produk kayu komposit dan laminating adhesive, dengan syarat  memiliki kadar emisi formaldehida rendah, yang ditandai dengan label/sertifikasi yang diakui GBC Indonesia','score',1,'',true,NOW(),NOW(),'system'),
(79, 33, 'IHC 3-3','Menggunakan material lampu yang kandungan merkurinya pada toleransi maksimum yang disetujui GBC Indonesia dan tidak menggunakan material yang mengandung asbestos.','score',1,'',true,NOW(),NOW(),'system'),
(80, 34, 'IHC 4','Apabila 75% dari net lettable area (NLA) menghadap langsung ke pemandangan luar yang dibatasi bukaan transparan bila ditarik suatu garis lurus','score',1,'Mungkin/ Tidak
Persentase NLA : ………………',false,NOW(),NOW(),'system'),
(81, 35, 'IHC 5','Menggunakan lampu dengan iluminansi (tingkat pencahayaan) ruangan sesuai dengan SNI 03-6197-2011  Tentang Konservasi Energi pada Sistem Pencahayaan.','score',1,'',true,NOW(),NOW(),'system'),
(82, 36, 'IHC 6','Menetapkan perencanaan kondisi termal ruangan secara umum pada suhu 250C dan kelembaban relatif 60%','score',1,'Ya / Tidak
Jenis AC:
(bila bukan central) Mekanisme: ………………………………………………',false,NOW(),NOW(),'system'),
(83, 37, 'IHC 7','Tingkat kebisingan pada 90% dari nett lettable area (NLA) tidak lebih dari atau sesuai dengan SNI 03-6386-2000,  tentang Spesifikasi Tingkat Bunyi dan waktu Dengung dalam Bangunan Gedung dan Perumahan (kriteria desain yang direkomendasikan).','score',1,'',true,NOW(),NOW(),'system'),
(84, 38, 'BEM P','Adanya instalasi atau fasilitas untuk memilah dan mengumpulkan sampah sejenis sampah rumah tangga (UU No. 18 Tahun 2008) berdasarkan jenis organik, anorganik dan B3','prequisite',null,'Jenis Fasilitas : …………….
Di luar / dlm gedung
Di luar / dlm area
Mandiri / pihak ke-tiga',false,NOW(),NOW(),'system'),
(85, 39, 'BEM 1','Melibatkan minimal seorang tenaga ahli yang sudah bersertifikat GREENSHIP Professional (GP), yang bertugas untuk  memandu proyek  hingga mendapatkan  sertifikat GREENSHIP.','score',1,'Nama/Perusahaan : ……………',false,NOW(),NOW(),'system'),
(86, 40, 'BEM 2-1','Limbah padat, dengan menyediakan area pengumpulan, pemisahan, dan sistem pencatatan. Pencatatan dibedakan berdasarkan limbah padat yang dibuang ke TPA, digunakan kembali, dan didaur ulang oleh pihak ketiga.','score',1,'',true,NOW(),NOW(),'system'),
(87, 40, 'BEM 2-2','Limbah cair, dengan menjaga kualitas seluruh buangan air yang timbul dari aktivitas konstruksi agar tidak mencemari drainase kota','score',1,'',true,NOW(),NOW(),'system'),
(88, 41, 'BEM 3-1','Mengolah limbah organik  gedung   yang dilakukan secara mandiri maupun bekerja sama dengan pihak ketiga , sehingga menambah nilai manfaat dan dapat mengurangi dampak lingkungan.','score',1,'Metode :……………
Mandiri / pihak ke-tiga
Di luar / dlm gedung
Di luar / dlm area',false,NOW(),NOW(),'system'),
(89, 41, 'BEM 3-2','Mengolah limbah an-organik gedung  yang dilakukan secara mandiri maupun bekerja sama dengan pihak ketiga, sehingga menambah nilai manfaat dan dapat mengurangi dampak lingkungan.','score',1,'Metode :……………
Mandiri / pihak ke-tiga
Di luar / dlm gedung
Di luar / dlm area
',false,NOW(),NOW(),'system'),
(90, 42, 'BEM 4-1','Melakukan prosedur testing- commissioning sesuai dengan petunjuk GBC Indonesia, termasuk pelatihan terkait untuk optimalisasi kesesuaian fungsi dan kinerja peralatan/sistem dengan perencanaan dan acuannya.','score',2,'
Metode yg digunakan: …………..
Pihak ke-3 yg terlibat : …………..
Jadwal selesai pemasangan AC :
Jadwal TC :………….',false,NOW(),NOW(),'system'),
(91, 42, 'BEM 4-2','Memastikan seluruh measuring adjusting instrument telah terpasang pada saat konstruksi dan memperhatikan kesesuaian antara desain dan spesifikasi teknis terkait komponen propper commissioning.','score',1,'Pihak ke-3 terlibat sejak tahap : ……….',false,NOW(),NOW(),'system'),
(92, 43, 'BEM 5-1','Menyerahkan data implementasi green building sesuai dengan form dari GBC Indonesia.','score',1,'',true,NOW(),NOW(),'system'),
(93, 43, 'BEM 5-2','Memberi pernyataan bahwa pemilik gedung akan menyerahkan data implementasi green building dari bangunannya dalam waktu 12 bulan setelah tanggal sertifikasi kepada GBC Indonesia dan suatu pusat data energi Indonesia yang akan ditentukan kemudian','score',1,'',true,NOW(),NOW(),'system'),
(94, 44, 'BEM 6','Memiliki surat perjanjian dengan penyewa gedung (tenant) untuk gedung yang disewakan atau SPO untuk gedung yang digunakan sendiri, yang terdiri atas:','score',1,'',true,NOW(),NOW(),'system'),
(95, 45, 'BEM 7','Memberi pernyataan bahwa pemilik gedung akan mengadakan survei suhu dan kelembaban paling lambat 12 bulan setelah tanggal sertifikasi dan menyerahkan laporan hasil survei paling lambat 15 bulan setelah tanggal sertifikasi kepada GBC Indonesia.','score',2,'',true,NOW(),NOW(),'system');

INSERT INTO `master_documents`
VALUES
(1, 1, 'ASD P1','Perhitungan rencana yang menunjukan persentase area lansekap berupa vegetasi (softscape).',NOW(),NOW(),'system'),
(2, 1, 'ASD P1','Gambar Design Development lansekap yang memuat informasi area dasar hijau.',NOW(),NOW(),'system'),
(3, 1, 'ASD P1','Gambar tender potongan tapak lahan proyek yang dapat menunjukkan posisi basement*.',NOW(),NOW(),'system'),
(4, 1, 'ASD P1','Gambar tender denah lantai dasar yang menunjukkan garis batas basement*. ',NOW(),NOW(),'system'),
(5, 2, 'ASD P2','Perhitungan rencana yang menunjukan komposisi vegetasi.',NOW(),NOW(),'system'),
(6, 2, 'ASD P2','Gambar skematik (Design Development) lansekap yang memuat informasi mengenai formasi vegetasi.',NOW(),NOW(),'system'),
(7, 2, 'ASD P2','Daftar vegetasi yang direncanakan akan digunakan pada lahan mencakup informasi jenis tanaman, luas tajuk dan fungsinya, yang sesuai dengan:
o Komposisi vegetasi mengikuti Peraturan Menteri Dalam Negeri  No.1 tahun 2007 pasal 13 (2a) 
o Vegetasi memiliki kriteria berdasarkan Peraturan Menteri Pekerjaan Umum No. 5/PRT/M/2008
',NOW(),NOW(),'system'),
(8, 3, 'ASD 1-1A','Peta lokasi / gambar yang menunjukkan adanya prasarana dan sarana pada tolok ukur.',NOW(),NOW(),'system'),
(9, 4, 'ASD 1-1B','Dokumen yang menunjukkan pembangunan dilakukan pada lahan peruntukan dengan KLB > 3.',NOW(),NOW(),'system'),
(10, 5, 'ASD 1-2','Laporan perencanaan revitalisasi, yang dilengkapi data berupa:
o Penjelasan rencana revitalisasi
o Foto lokasi pra pembangunan 
o Gambar tender rencana revitalisasi pada area yang dimaksud
o Jika menyangkut revitalisasi area relatif kumuh maka perlu disampaikan juga bukti metode revitalisasi dan persetujuan komunitas yang terkena program revitalisasi area tersebut yang representatif dan sah secara hukum',NOW(),NOW(),'system'),
(11, 6, 'ASD 2-1','Peta lokasi yang menunjukkan lokasi fasilitas umum berikut dengan jarak tempuhnya. ',NOW(),NOW(),'system'),
(12, 7, 'ASD 2-2','Peta lokasi rencana yang menunjukkan rencana sirkulasi akses pejalan kaki dan lokasi fasilitas umum berikut dengan jarak tempuhnya.',NOW(),NOW(),'system'),
(13, 7, 'ASD 2-2','Gambar tender rencana tapak yang menunjukkan akses pejalan kaki yang dibuka untuk menuju fasilitas umum.',NOW(),NOW(),'system'),
(14, 8, 'ASD 2 - 3','Peta lokasi yang menunjukkan fasilitas/akses bebas dari perpotongan kendaraan bermotor dan lokasi  fasilitas umum berikut dengan jarak tempuhnya.',NOW(),NOW(),'system'),
(15, 8, 'ASD 2 - 3','Gambar tender rencana tapak yang menunjukkan fasilitas/akses bebas dari perpotongan kendaraan bermotor.',NOW(),NOW(),'system'),
(16, 9, 'ASD 2 - 4','Surat pernyataan dari pemilik gedung akan membuka lantai dasar gedung untuk akses pejalan kaki selama 10 jam/hari.',NOW(),NOW(),'system'),
(17, 9, 'ASD 2 - 4','Gambar tender yang menunjukkan lantai dasar gedung yang dibuka untuk akses pejalan kaki.',NOW(),NOW(),'system'),
(18, 10, 'ASD 3 - 1A','Peta lokasi yang menunjukkan letak halte atau stasiun transportasi umum dari gerbang lokasi bangunan, berikut dengan jarak tempuhnya.',NOW(),NOW(),'system'),
(19, 10, 'ASD 3 - 1A','Informasi trayek transportasi umum yang tersedia.',NOW(),NOW(),'system'),
(20, 11, 'ASD 3 - 1B','Surat pernyataan dari pemilik gedung mengenai penyediaan shuttle bus sesuai dengan perhitungan tolok ukur.',NOW(),NOW(),'system'),
(21, 11, 'ASD 3 - 1B','Daftar trayek untuk shuttle bus yang direncanakan.',NOW(),NOW(),'system'),
(22, 12, 'ASD 3 - 2','Gambar tender desain jalur pedestrian.',NOW(),NOW(),'system'),
(23, 12, 'ASD 3 - 2','Gambar tender rencana tapak yang menunjukan letak jalur pedestrian.',NOW(),NOW(),'system'),
(24, 13, 'ASD 4 - 1','Gambar tender perletakan tempat parkir sepeda.',NOW(),NOW(),'system'),
(25, 13, 'ASD 4 - 1','Rencana kerja dan syarat-syarat atau gambar tender yang menunjukan rencana desain/tipe tempat parkir sepeda.',NOW(),NOW(),'system'),
(26, 14, 'ASD 4 - 2','Gambar tender yang menunjukkan rencana peletakan shower untuk pengguna sepeda.',NOW(),NOW(),'system'),
(27, 14, 'ASD 4 - 2','Gambar tender yang menunjukkan rencana desain shower untuk pengguna sepeda.',NOW(),NOW(),'system'),
(29, 15, 'ASD 5 -1A','Perhitungan rencana yang menunjukan persentase area lansekap berupa softscape.',NOW(),NOW(),'system'),
(30, 15, 'ASD 5 -1A','Gambar Design Development rencana lansekap yang memuat informasi mengenai formasi vegetasi.',NOW(),NOW(),'system'),
(31, 16, 'ASD 5 -1B','Perhitungan rencana yang menunjukan persentase area lansekap berupa softscape.',NOW(),NOW(),'system'),
(32, 16, 'ASD 5 -1B','Gambar Design Development rencana lansekap yang memuat informasi mengenai formasi vegetasi.',NOW(),NOW(),'system'),
(33, 17, 'ASD 5 -2','Perhitungan rencana yang menunjukan total luas tajuk tanaman lokal dan budidaya lokal.',NOW(),NOW(),'system'),
(35, 17, 'ASD 5 -2','Daftar vegetasi yang direncanakan akan digunakan mencakup informasi jenis tanaman, luas tajuk serta asal dan tempat budi dayanya.',NOW(),NOW(),'system'),
(36, 18, 'ASD 6 - 1A','Perhitungan rencana albedo atap keseluruhan beserta sumber nilai albedonya. ',NOW(),NOW(),'system'),
(37, 18, 'ASD 6 - 1A','Gambar tender rencana atap yang menunjukkan material atap yang digunakan.',NOW(),NOW(),'system'),
(38, 18, 'ASD 6 - 1A','Rencana kerja dan syarat-syarat yang menunjukan material atap yang digunakan.',NOW(),NOW(),'system'),
(39, 19, 'ASD 6 - 1B','Perhitungan luas rencana green roof.',NOW(),NOW(),'system'),
(40, 19, 'ASD 6 - 1B','Gambar tender rencana atap yang menunjukkan green roof.',NOW(),NOW(),'system'),
(41, 20, 'ASD 6 - 2','Perhitungan rencana albedo perkerasan area non-atap keseluruhan beserta sumber nilai albedonya. ',NOW(),NOW(),'system'),
(42, 20, 'ASD 6 - 2','Gambar tender rencana non-atap yang menunjukkan material perkerasan yang digunakan.',NOW(),NOW(),'system'),
(43, 20, 'ASD 6 - 2','Rencana kerja dan syarat-syarat yang menunjukan material perkerasan non-atap yang digunakan.',NOW(),NOW(),'system'),
(44, 21, 'ASD 6 - 3A','Laporan singkat yang menjelaskan aplikasi desain pelindung dari panas akibat radiasi matahari yang direncanakan, mencakup: Penjelasan pengaruh aplikasi desain terhadap sirkulasi utama pejalan kaki (bisa berupa skema, simulasi dan lain sebagainya)',NOW(),NOW(),'system'),
(45, 21, 'ASD 6 - 3A','Laporan singkat yang menjelaskan aplikasi desain pelindung dari panas akibat radiasi matahari yang direncanakan, mencakup: Gambar tender rencana jalur pedestrian yang menunjukkan adanya pelindung dari panas akibat radiasi matahari pada sirkulasi utama pejalan kaki',NOW(),NOW(),'system'),
(46, 22, 'ASD 6 - 3B','Laporan singkat yang menjelaskan aplikasi desain pelindung dari terpaan angin kencang yang direncanakan, mencakup: Penjelasan pengaruh aplikasi desain terhadap sirkulasi utama pejalan kaki (bisa berupa skema, simulasi dan lain sebagainya)',NOW(),NOW(),'system'),
(47, 22, 'ASD 6 - 3B','Laporan singkat yang menjelaskan aplikasi desain pelindung dari terpaan angin kencang yang direncanakan, mencakup: Gambar tender rencana jalur pedestrian yang menunjukkan adanya pelindung dari terpaan angin kencang pada sirkulasi utama pejalan kaki',NOW(),NOW(),'system'),
(48, 23, 'ASD 7 - 1A','Perhitungan rencana volume air hujan yang ditangani dalam lahan proyek. ',NOW(),NOW(),'system'),
(49, 23, 'ASD 7 - 1A','Skema rencana penanganan air hujan meliputi: arah aliran air, proses pengolahan beserta pemanfaatannya.',NOW(),NOW(),'system'),
(50, 23, 'ASD 7 - 1A','Gambar tender fasilitas penanganan air hujan.',NOW(),NOW(),'system'),
(51, 24, 'ASD 7 - 1B','Perhitungan rencana volume air hujan yang ditangani dalam lahan proyek. ',NOW(),NOW(),'system'),
(52, 24, 'ASD 7 - 1B','Skema rencana penanganan air hujan meliputi: arah aliran air, proses pengolahan beserta pemanfaatannya.',NOW(),NOW(),'system'),
(53, 24, 'ASD 7 - 1B','Gambar tender fasilitas penanganan air hujan.',NOW(),NOW(),'system'),
(54, 25, 'ASD 7 - 2','Perhitungan rencana volume air hujan yang menunjukan penanganan dari luar lokasi bangunan yang ditangani. ',NOW(),NOW(),'system'),
(55, 25, 'ASD 7 - 2','Skema rencana penanganan beban banjir lingkungan dari luar lokasi bangunan  meliputi: arah aliran air, proses pengolahan beserta pemanfaatannya.',NOW(),NOW(),'system'),
(56, 25, 'ASD 7 - 2','Gambar tender fasilitas penanganan beban banjir lingkungan  dari luar lokasi lahan proyek.',NOW(),NOW(),'system'),
(57, 26, 'ASD 7 - 3','Gambar tender teknologi pengurangan debit limpasan air hujan.',NOW(),NOW(),'system'),
(58, 26, 'ASD 7 - 3','Gambar tender letak penempatan teknologi pengurangan debit limpasan air hujan.',NOW(),NOW(),'system'),
(59, 26, 'ASD 7 - 3','Laporan rencana penanganan, meliputi: skema rencana penanganan debit air hujan, deskripsi cara kerjanya dan pengaruhnya terhadap usaha mengurangi debit limpasan air hujan.',NOW(),NOW(),'system'),
(60, 27, 'EEC P1','Gambar tender skema distribusi listrik satu garis yang menunjukkan lokasi sub meter dan pemisahan beban sesuai dengan tolok ukur',NOW(),NOW(),'system'),
(61, 28, 'EEC P2','Dokumen perhitungan desain selimut bangunan berdasarkan SNI 03-6389-2011 tentang Konservasi Energi pada Bangunan Gedung.',NOW(),NOW(),'system'),
(62, 29, 'EEC 1-1','Dokumen perhitungan konsumsi energi gedung yang menggunakan energy modelling software berdasarkan spesifikasi desain yang tertuang dalam dokumen tender.',NOW(),NOW(),'system'),
(63, 30, 'EEC 1-2','Dokumen perhitungan konsumsi energi gedung yang menggunakan worksheet GBC Indonesia berdasarkan spesifikasi desain yang tertuang dalam dokumen tender.',NOW(),NOW(),'system'),
(64, 31, 'EEC 1-3-1-1','Perhitungan OTTV gedung dan selisihnya dengan nilai 35 watt/m2 berdasarkan spesifikasi material selimut bangunan yang ada dalam dokumen tender',NOW(),NOW(),'system'),
(65, 32, 'EEC 1-3-1-2','Perhitungan OTTV gedung dan selisihnya dengan nilai 35 watt/m2 berdasarkan spesifikasi material selimut bangunan yang ada dalam dokumen tender',NOW(),NOW(),'system'),
(66, 33, 'EEC 1-3-2-1','Gambar tender rencana ME lampu yang menunjukkan jenis lampu beserta spesifikasi daya yang digunakan ',NOW(),NOW(),'system'),
(67, 33, 'EEC 1-3-2-1','Perhitungan daya per luasan berdasarkan spesifikasi daya pada lampu yang digunakan dalam dokumen tender',NOW(),NOW(),'system'),
(68, 34, 'EEC 1-3-2-2','Gambar tender rencana ME lampu yang menunjukkan jenis lampu beserta spesifikasi daya yang digunakan ',NOW(),NOW(),'system'),
(69, 34, 'EEC 1-3-2-2','Dokumen RKS yang menunjukkan penggunaan lampu yang memiliki ballast elektronik',NOW(),NOW(),'system'),
(70, 35, 'EEC 1-3-2-3','Gambar tender rencana ME lampu yang menunjukkan peletakan sensor gerak.',NOW(),NOW(),'system'),
(71, 36, 'EEC 1-3-2-4','Gambar tender rencana ME lampu yang menunjukkan peletakan saklar.',NOW(),NOW(),'system'),
(72, 37, 'EEC 1-3-3-1','Dokumen RKS yang memuat penggunaan elevator yang memiliki traffic analysis atau menggunakan regenerative drive system atau eskalator yang menggunakan sleep mode.',NOW(),NOW(),'system'),
(73, 38, 'EEC 1-3-3-2','Dokumen RKS yang memuat penggunaan elevator yang memiliki traffic analysis atau menggunakan regenerative drive system atau eskalator yang menggunakan sleep mode.',NOW(),NOW(),'system'),
(74, 39, 'EEC 1-3-4','Dokumen RKS yang memuat penggunaan sistem pendingin dengan COP di atas SNI atau standar lainnya.',NOW(),NOW(),'system'),
(75, 40, 'EEC 2-1','Dokumen tender denah yang menunjukkan lokasi bukaan transparan',NOW(),NOW(),'system'),
(76, 40, 'EEC 2-1','Perhitungan secara manual /software',NOW(),NOW(),'system'),
(77, 40, 'EEC 2-1','Dokumen tampak yang menunjukkan sisi bukaan',NOW(),NOW(),'system'),
(78, 40, 'EEC 2-1','Dokumen tender detail yang menunjukkan detail bukaan transparan (opsional atau hanya disiapkan jika tim verifikasi GBCI memerlukan dokumen tersebut)',NOW(),NOW(),'system'),
(79, 41, 'EEC 2-2','Gambar tender yang menunjukkan zonasi pencahayaan',NOW(),NOW(),'system'),
(80, 41, 'EEC 2-2','Rencana kerja dan syarat-syarat yang menunjukkan spesifikasi lux sensor',NOW(),NOW(),'system'),
(81, 42, 'EEC 3','Gambar tender denah yang menunjukkan lokasi yang tidak menggunakan AC beserta jenis ventilasinya',NOW(),NOW(),'system'),
(82, 42, 'EEC 3','Gambar tender yang menunjukkan detail ventilasi alami',NOW(),NOW(),'system'),
(83, 42, 'EEC 3','Rencana kerja dan syarat-syarat yang menunjukkan spesifikasi teknik ventilasi mekanik',NOW(),NOW(),'system'),
(84, 43, 'EEC 4','Dokumen Perhitungan CCI berdasarkan dokumen tender',NOW(),NOW(),'system'),
(85, 44, 'EEC 5','Dokumen gambar tender yang menunjukkan lokasi energi terbarukan pada gedung',NOW(),NOW(),'system'),
(86, 44, 'EEC 5','Perhitungan perencanaan penghematan yang dicapai melalui energi terbarukan',NOW(),NOW(),'system'),
(87, 44, 'EEC 5','Rencana kerja dan syarat-syarat yang menunjukkan spesifiksi teknologi energi terbarukan',NOW(),NOW(),'system'),
(88, 45, 'WAC P','Gambar tender skema distribusi air satu garis yang menunjukkan pemisahan sesuai dengan tolok ukur dan lokasi sub meter',NOW(),NOW(),'system'),
(89, 46, 'WAC P2','Perhitungan worksheet sesuai dengan spesifikasi dalam dokumen tender',NOW(),NOW(),'system'),
(90, 47, 'WAC 1-1','Perhitungan penggunaan air sesuai dengan spesifikasi dalam dokumen tender',NOW(),NOW(),'system'),
(91, 48, 'WAC 1-2','Perhitungan penggunaan air sesuai dengan spesifikasi dalam dokumen tender',NOW(),NOW(),'system'),
(92, 49, 'WAC 2-1A','Rencana kerja dan syarat-syarat yang menunjukkan spesifikasi fitur air',NOW(),NOW(),'system'),
(93, 50, 'WAC 2-1B','Perhitungan kuantitas fitur air sesuai dengan spesifikasi dalam dokumen tender',NOW(),NOW(),'system'),
(94, 51, 'WAC 2-1C','Minimal 25 % = 1 Poin , Minimal 50% = 2 Poin, Minimal 75 % = 3 Poin',NOW(),NOW(),'system'),
(95, 52, 'WAC 3-1A','Gambar tender fasilitas daur ulang air',NOW(),NOW(),'system'),
(96, 53, 'WAC 3 - 1B','Dokumen rencana spesifikasi instalasi daur ulang air  dan kualitas air hasil pengolahan',NOW(),NOW(),'system'),
(97, 54, 'WAC 4-1A','Gambar tender yang menunjukkan instalasi pengambilan air alternatif dan distribusinya',NOW(),NOW(),'system'),
(98, 55, 'WAC 4-1B','Gambar tender yang menunjukkan instalasi pengambilan air alternatif dan distribusinya',NOW(),NOW(),'system'),
(99, 56, 'WAC 4-1C','Gambar tender yang menunjukkan instalasi pengambilan air alternatif dan distribusinya',NOW(),NOW(),'system'),
(100, 57, 'WAC 5-1A','Perhitungan kapasitas  penampungan air hujan berdasarkan dokumen tender',NOW(),NOW(),'system'),
(101, 57, 'WAC 5-1A','Gambar tender penampungan air hujan ',NOW(),NOW(),'system'),
(102, 58, 'WAC 5-1B','Skema pengambilan, penampungan, dan pemanfaatan air hujan',NOW(),NOW(),'system'),
(103, 59, 'WAC 5-1C','Kapasitas 50 % = 1 Poin, Kapasitas 75 % = 2 Poin, Kapasitas 100 % = 3 Poin',NOW(),NOW(),'system'),
(104, 60, 'WAC 6-1','Perhitungan konsumsi air untuk irigasi berdasarkan kalkulator WAC sesuai dengan dokumen tender ',NOW(),NOW(),'system'),
(105, 60, 'WAC 6-1','Skema rencana irigasi tanaman meliputi (sumber air, cara penyiraman)',NOW(),NOW(),'system'),
(106, 60, 'WAC 6-1','Gambar tender area yang menggunakan irigasi pada gedung',NOW(),NOW(),'system'),
(107, 61, 'WAC 6-2','Rencana kerja dan syarat-syarat yang menunjukkan spesifikasi teknologi irigasi yang sesuai dengan kebutuhan tanaman',NOW(),NOW(),'system'),
(108, 62, 'MRC P','Dokumen Rencana Kerja dan Syarat-syarat yang menunjukkan rencana untuk tidak menggunakan chloro fluoro carbon (CFC) sebagai refrigeran dan halon sebagai bahan pemadam kebakaran.',NOW(),NOW(),'system'),
(109, 68, 'MRC 3','Dokumen Rencana Kerja dan Syarat-syarat yang menunjukkan rencana bahwa penggunaan produk sistem pendingin yang refrigerannya  tidak memiliki potensi merusak ozon atau ODP sama dengan nol.',NOW(),NOW(),'system'),
(110, 74, 'IHC P','Perhitungan yang menunjukkan rencana jumlah introduksi udara luar sesuai dengan ASHRAE 62.1.2007 atau standar ASHRAE terbaru.',NOW(),NOW(),'system'),
(111, 75, 'IHC 1','Gambar tender rencana mekanikal elektrikal yang menunjukkan peletakkan sensor CO2',NOW(),NOW(),'system'),
(112, 75, 'IHC 1','Deskripsi rencana skema pengaturan CO2 monitoring',NOW(),NOW(),'system'),
(113, 76, 'IHC 2','Untuk gedung tanpa Area Merokok',NOW(),NOW(),'system'),
(114, 76, 'IHC 2','Dokumen Rencana Kerja dan Syarat-syarat (RKS) dari pemilik gedung yang menunjukkan klausul bahwa akan ada ketentuan larangan merokok',NOW(),NOW(),'system'),
(115, 76, 'IHC 2','Dokumen tender denah yang menunjukkan peletakkan tanda dilarang merokok di dalam proyek',NOW(),NOW(),'system'),
(116, 76, 'IHC 2','Untuk Gedung dengan Area Merokok',NOW(),NOW(),'system'),
(117, 76, 'IHC 2','Dokumen tender denah yang menunjukkan jarak area merokok terhadap pintu masuk, outdoor air intake, dan bukaan jendela (jika terdapat area merokok yang terpisah dari gedung)',NOW(),NOW(),'system'),
(118, 77, 'IHC 4','Dokumen tender denah yang menunjukkan peletakan bukaan transparan',NOW(),NOW(),'system'),
(119, 78, 'IHC 4','Dokumen tender detail bukaan transparan',NOW(),NOW(),'system'),
(120, 79, 'IHC 4','Perhitungan persentase luas ruang aktif yang mendapat pemandangan luar secara langsung',NOW(),NOW(),'system'),
(121, 82, 'IHC 6','Dokumen yang menunjukkan bahwa input data dalam perhitungan cooling load menggunakan suhu dan kelembaban relatif sesuai tolok ukur',NOW(),NOW(),'system'),
(122, 84, 'BEM P','Surat pernyataan bahwa pemilik gedung akan melakukan pemisahan sampah berdasarkan organik, anorganik dan B3 dari dalam gedung sampai keluar lahan gedung.',NOW(),NOW(),'system'),
(123, 84, 'BEM P','Draft SPO / dokumen perencanaan yang menunjukkan rencana pemisahan sampah dari dalam gedung ke luar lahan gedung.',NOW(),NOW(),'system'),
(124, 84, 'BEM P','Gambar tender yang menunjukkan peletakan fasilitas sampah terpisah dalam gedung dan di dalam lahan gedung.',NOW(),NOW(),'system'),
(125, 85, 'BEM 1','Surat penunjukkan GP yang terlibat yang ditandatangani oleh pemilik proyek.',NOW(),NOW(),'system'),
(126, 85, 'BEM 1','Fotokopi sertifikat GP yang terlibat dalam proyek.',NOW(),NOW(),'system'),
(127, 88, 'BEM 3-1','Surat pernyataan bahwa pemilik gedung akan melakukan pengolahan limbah organik secara mandiri/dengan pihak ketiga.',NOW(),NOW(),'system'),
(128, 88, 'BEM 3-1','Jika perencanaan pengolahan dilakukan secara mandiri: Dokumen / gambar tender yang menunjukkan instalasi pengomposan.',NOW(),NOW(),'system'),
(129, 88, 'BEM 3-1','Bukti fotografis instalasi pengomposan.',NOW(),NOW(),'system'),
(130, 89, 'BEM 3-2','Surat pernyataan bahwa pemilik gedung akan melakukan pengelolaan limbah anorganik dengan pihak ketiga.',NOW(),NOW(),'system'),
(131, 90, 'BEM 4-1','Surat penunjukkan perorangan/tim sebagai penanggungjawab proses komisioning serta cakupan kerjaan yang sesuai dengan kebutuhan komisioning menurut GBC Indonesia, dan ditandatangani oleh pemilik gedung.',NOW(),NOW(),'system'),
(132, 90, 'BEM 4-1','Dokumen perencanaan pelaksanaan TC berdasarkan panduan yang tentukan GBC Indonesia yang disesuaikan dengan Owners Project Requirements (OPR) , yang berisi definisi, overview, cakupan, tim dan tanggungjawab, prosedur, pelatihan, dokumentasi dan jadwal.',NOW(),NOW(),'system'),
(133, 91, 'BEM 4-2','Gambar tender mekanikal elektrikal yang menunjukkan letak pemasangan peralatan measuring adjusting instrument.',NOW(),NOW(),'system');


INSERT INTO `master_criteria_blockers`
VALUES
(1, 3, 4 , NOW(), NOW(),'system'),
(2, 4, 3 , NOW(), NOW(),'system'),
(3, 10, 11 , NOW(), NOW(),'system'),
(4, 11, 10 , NOW(), NOW(),'system'),
(5, 18, 19 , NOW(), NOW(),'system'),
(6, 19, 18 , NOW(), NOW(),'system'),
(7, 21, 22 , NOW(), NOW(),'system'),
(8, 22, 21 , NOW(), NOW(),'system'),
(9, 23, 24 , NOW(), NOW(),'system'),
(10, 24, 23 , NOW(), NOW(),'system'),
(11, 49, 50 , NOW(), NOW(),'system'),
(12, 49, 51 , NOW(), NOW(),'system'),
(13, 50, 49 , NOW(), NOW(),'system'),
(14, 50, 51 , NOW(), NOW(),'system'),
(15, 51, 49 , NOW(), NOW(),'system'),
(16, 51, 50 , NOW(), NOW(),'system'),
(17, 52, 53 , NOW(), NOW(),'system'),
(18, 53, 52 , NOW(), NOW(),'system'),
(19, 54, 55 , NOW(), NOW(),'system'),
(20, 54, 56 , NOW(), NOW(),'system'),
(21, 55, 54 , NOW(), NOW(),'system'),
(22, 55, 56 , NOW(), NOW(),'system'),
(23, 56, 54 , NOW(), NOW(),'system'),
(24, 56, 55 , NOW(), NOW(),'system'),
(25, 57, 58 , NOW(), NOW(),'system'),
(26, 57, 59 , NOW(), NOW(),'system'),
(27, 58, 57 , NOW(), NOW(),'system'),
(28, 58, 59 , NOW(), NOW(),'system'),
(29, 59, 57 , NOW(), NOW(),'system'),
(30, 59, 58 , NOW(), NOW(),'system'),
(31, 63, 64 , NOW(), NOW(),'system'),
(32, 64, 63 , NOW(), NOW(),'system'),
(33, 29, 30	, NOW(), NOW(),'system'),
(34, 29, 31	, NOW(), NOW(),'system'),
(35, 29, 32	, NOW(), NOW(),'system'),
(36, 29, 33	, NOW(), NOW(),'system'),
(37, 29, 34	, NOW(), NOW(),'system'),
(38, 29, 35	, NOW(), NOW(),'system'),
(39, 29, 39	, NOW(), NOW(),'system'),
(40, 29, 37	, NOW(), NOW(),'system'),
(41, 29, 38	, NOW(), NOW(),'system'),
(42, 29, 36	, NOW(), NOW(),'system'),
(43, 30, 29	, NOW(), NOW(),'system'),
(44, 30, 31	, NOW(), NOW(),'system'),
(45, 30, 32	, NOW(), NOW(),'system'),
(46, 30, 35	, NOW(), NOW(),'system'),
(47, 30, 38	, NOW(), NOW(),'system'),
(48, 30, 37	, NOW(), NOW(),'system'),
(49, 30, 34	, NOW(), NOW(),'system'),
(50, 30, 33	, NOW(), NOW(),'system'),
(51, 30, 36	, NOW(), NOW(),'system'),
(52, 30, 39	, NOW(), NOW(),'system'),
(53, 31, 29	, NOW(), NOW(),'system'),
(54, 31, 30	, NOW(), NOW(),'system'),
(55, 32, 30	, NOW(), NOW(),'system'),
(56, 32, 29	, NOW(), NOW(),'system'),
(57, 33, 29	, NOW(), NOW(),'system'),
(58, 33, 30	, NOW(), NOW(),'system'),
(59, 34, 29	, NOW(), NOW(),'system'),
(60, 34, 30	, NOW(), NOW(),'system'),
(61, 35, 29	, NOW(), NOW(),'system'),
(62, 35, 30	, NOW(), NOW(),'system'),
(63, 36, 29	, NOW(), NOW(),'system'),
(64, 36, 30	, NOW(), NOW(),'system'),
(65, 37, 29	, NOW(), NOW(),'system'),
(66, 37, 30	, NOW(), NOW(),'system'),
(67, 38, 29	, NOW(), NOW(),'system'),
(68, 38, 30	, NOW(), NOW(),'system'),
(69, 39, 29	, NOW(), NOW(),'system'),
(70, 39, 30	, NOW(), NOW(),'system');