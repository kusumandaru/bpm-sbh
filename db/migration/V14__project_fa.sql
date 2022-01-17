INSERT INTO `master_templates`
VALUES
  (2, 1, 'final_assessment', 'GREENSHIP NB 1.2 Exercise ', NOW(), NOW(),'system');

INSERT INTO `master_evaluations`
VALUES
  (7, 2, 'ASD', 'Appropiate Site Development', NOW(), NOW(),'system'),
  (8, 2, 'EEC', 'Energy Effeciency and Conservation', NOW(), NOW(),'system'),
  (9, 2, 'WAC', 'Water Conservation', NOW(), NOW(),'system'),
  (10, 2, 'MRC', 'Material Resource and Cycle', NOW(), NOW(),'system'),
  (11, 2, 'IHC', 'Indoor Health and Comfort', NOW(), NOW(),'system'),
  (12, 2, 'BEM', 'Building Environment Management', NOW(), NOW(),'system');

INSERT INTO `master_exercises`
VALUES
  (46, 7, 'prequisite','ASD P','Basic Green Area',null,NOW(),NOW(),'system'),
  (47, 7, 'score','ASD 1','Site Selection',2,NOW(),NOW(),'system'),
  (48, 7, 'score','ASD 2','Community Accessibility',2,NOW(),NOW(),'system'),
  (49, 7, 'score','ASD 3','Public Transportation',2,NOW(),NOW(),'system'),
  (50, 7, 'score','ASD 4','Bicycle ',2,NOW(),NOW(),'system'),
  (51, 7, 'score','ASD 5','Site Landscaping',3,NOW(),NOW(),'system'),
  (52, 7, 'score','ASD 6','Micro Climate',3,NOW(),NOW(),'system'),
  (53, 7, 'score','ASD 7','Storm Water Management',3,NOW(),NOW(),'system'),

  (54, 8, 'prequisite','EEC P1','Electrical Sub Metering',null,NOW(),NOW(),'system'),
  (55, 8, 'prequisite','EEC P2','OTTV Calculation',null,NOW(),NOW(),'system'),
  (56, 8, 'score','EEC 1','Energy Efficiency Measure',20,NOW(),NOW(),'system'),
  (57, 8, 'score','EEC 2','Natural Lighting',4,NOW(),NOW(),'system'),
  (58, 8, 'score','EEC 3','Ventilation ',1,NOW(),NOW(),'system'),
  (59, 8, 'score','EEC 4','Climate Change Impact',1,NOW(),NOW(),'system'),
  (60, 8, 'score','EEC 5','On Site Renewable Energy',5,NOW(),NOW(),'system'),

  (61, 9, 'prequisite','WAC P','Water Metering',null,NOW(),NOW(),'system'),
  (62, 9, 'score','WAC 1','Water Use Reduction',8,NOW(),NOW(),'system'),
  (63, 9, 'score','WAC 2','Water Fixtures',3,NOW(),NOW(),'system'),
  (64, 9, 'score','WAC 3','Water Recycling',3,NOW(),NOW(),'system'),
  (65, 9, 'score','WAC 4','Alternative Water Resource',2,NOW(),NOW(),'system'),
  (66, 9, 'score','WAC 5','Rainwater Harvesting',3,NOW(),NOW(),'system'),
  (67, 9, 'score','WAC 6','Water Efficiency Landscaping',2,NOW(),NOW(),'system'),

  (68, 10, 'prequisite','MRC P','Fundamental Refrigerant',null,NOW(),NOW(),'system'),
  (69, 10, 'score','MRC 1','Building and Material Reuse',2,NOW(),NOW(),'system'),
  (70, 10, 'score','MRC 2','Environmentally Processed Product',3,NOW(),NOW(),'system'),
  (71, 10, 'score','MRC 3','Non ODS Usage',2,NOW(),NOW(),'system'),
  (72, 10, 'score','MRC 4','Certified Wood',2,NOW(),NOW(),'system'),
  (73, 10, 'score','MRC 5','Prefab Material',3,NOW(),NOW(),'system'),
  (74, 10, 'score','MRC 6','Regional Material',2,NOW(),NOW(),'system'),

  (75, 11, 'prequisite','IHC P','Outdoor Air Introduction',null,NOW(),NOW(),'system'),
  (76, 11, 'score','IHC 1','CO2 Monitoring',1,NOW(),NOW(),'system'),
  (77, 11, 'score','IHC 2','Environmental Tobacco Smoke Control',2,NOW(),NOW(),'system'),
  (78, 11, 'score','IHC 3','Chemical Pollutants',3,NOW(),NOW(),'system'),
  (79, 11, 'score','IHC 4','Outside View',1,NOW(),NOW(),'system'),
  (80, 11, 'score','IHC 5','Visual Comfort',1,NOW(),NOW(),'system'),
  (81, 11, 'score','IHC 6','Thermal Comfort',1,NOW(),NOW(),'system'),
  (82 , 11, 'score','IHC 7','Acoustic Level',1,NOW(),NOW(),'system'),

  (83, 12, 'prequisite','BEM P','Basic Waste Management',null,NOW(),NOW(),'system'),
  (84, 12, 'score','BEM 1','GP as a Member of The Project Team',1,NOW(),NOW(),'system'),
  (85, 12, 'score','BEM 2','Pollution of Construction Activity',2,NOW(),NOW(),'system'),
  (86, 12, 'score','BEM 3','Advanced Waste Management',2,NOW(),NOW(),'system'),
  (87, 12, 'score','BEM 4','Proper Commissioning',3,NOW(),NOW(),'system'),
  (88, 12, 'score','BEM 5','Green Building Data Submission',2,NOW(),NOW(),'system'),
  (89, 12, 'score','BEM 6','Fit Out Agreement',1,NOW(),NOW(),'system'),
  (90 , 12, 'score','BEM 7','Occupant Survey',2,NOW(),NOW(),'system');


INSERT INTO `master_criterias`
VALUES
(96, 46, 'ASD P1','Adanya area lansekap berupa vegetasi (softscape) yang bebas dari struktur bangunan dan struktur sederhana bangunan taman (hardscape) di atas permukaan tanah atau di bawah tanah.                                                            
o Untuk konstruksi baru, luas areanya adalah minimal 10% dari luas total lahan.                                                                                                                                                                             o Untuk  major renovation, luas areanya adalah minimal 50% dari ruang terbuka yang bebas basement dalam tapak.','prequisite',null,'Basement: Ada /Tdk
Luas Area : ………………
Luas RTH Bebas basement : …………………
Luas RTH Total : ……………',false,NOW(),NOW(),'system'),
(97, 46, 'ASD P2','Area ini memiliki vegetasi mengikuti Permendagri No 1 tahun 2007 Pasal 13 (2a) dengan komposisi 50% lahan tertutupi luasan pohon ukuran kecil, ukuran sedang, ukuran besar, perdu setengah pohon, perdu, semak dalam ukuran dewasa dengan jenis tanaman sesuai dengan Permen PU No. 5/PRT/M/2008 mengenai Ruang Terbuka Hijau (RTH) Pasal  2.3.1  tentang Kriteria Vegetasi untuk Pekarangan.','prequisite',null,'Luas Semak :……………..
Luas Tajuk perdu + semak : ………….
Luas Tajuk Pohon : ………………..',false,NOW(),NOW(),'system'),
(98, 47, 'ASD 1-1A','Memilih daerah pembangunan yang dilengkapi minimal delapan dari 12 prasarana sarana kota.
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
(99, 47, 'ASD 1-1B','Memilih daerah pembangunan dengan ketentuan KLB > 3. ','score',1,'',false,NOW(),NOW(),'system'),
(100, 47, 'ASD 1-2','Melakukan revitalisasi dan pembangunan di atas lahan yang bernilai negatif dan tak terpakai karena bekas pembangunan atau dampak negatif pembangunan.','score',1,'Sejarah :………………..',false,NOW(),NOW(),'system'),
(101, 48, 'ASD 2-1','Terdapat minimal tujuh jenis fasilitas umum dalam jarak pencapaian jalan utama sejauh 1500 m dari tapak.
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
(102, 48, 'ASD 2-2','Membuka akses pejalan kaki selain ke jalan utama di luar tapak yang menghubungkan-nya dengan jalan sekunder dan/atau lahan milik orang lain sehingga tersedia akses ke minimal 3 fasilitas umum sejauh 300 m jarak pencapaian pejalan kaki','score',1,'Jalan sekunder/
Daftar 3 Fasum:
1. ………………
2.………………
3.………………
',false,NOW(),NOW(),'system'),
(103, 48, 'ASD 2 - 3','Menyediakan fasilitas/akses yang aman, nyaman, dan bebas dari perpotongan dengan akses kendaraan bermotor untuk menghubungkan secara langsung bangunan dengan bangunan lain, di mana terdapat minimal 3 fasilitas umum dan/atau dengan stasiun transportasi masal','score',2,'Jenis Fasilitas/Akses:
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
(104, 48, 'ASD 2 - 4','Membuka lantai dasar gedung sehingga dapat menjadi akses pejalan kaki yang aman dan nyaman selama minimum 10 jam sehari','score',2,'Ya / Tidak',false,NOW(),NOW(),'system'),
(105, 49, 'ASD 3 - 1A','Adanya halte atau stasiun  transportasi umum dalam jangkauan 300 m (walking distance) dari gerbang lokasi bangunan dengan tidak memperhitungkan panjang jembatan penyeberangan dan ramp.','score',1,'Pintu yang digunakan berada di ………….
jarak dari pintu ke halte ….. : ± ……… m',false,NOW(),NOW(),'system'),
(106, 49, 'ASD 3 - 1B','Menyediakan shuttle bus untuk pengguna tetap gedung dengan jumlah unit minimum untuk 10% pengguna tetap gedung','score',1,'Jumlah okupan tetap : …………..
Jumlah seat min. yg diakomodasi : ……………..
Rencana jenis & Jumlah bus : …………..
Office  = Karyawan
mal = Karyawan
RS = Karyawan & Dokter tetap
Hotel = Karyawan
Apartemen = Karyawan
Sekolah = Karyawan + Dosen
Pabrik = Karyawan',false,NOW(),NOW(),'system'),
(107, 49, 'ASD 3 - 2','Menyediakan fasilitas jalur pedestrian di dalam area gedung untuk menuju ke stasiun transportasi umum terdekat yang aman dan nyaman  sesuai dengan Peraturan Menteri PU 30/PRT/M/2006 mengenai Pedoman Teknis Fasilitas dan Aksesibilitas pada Bangunan Gedung dan Lingkungan Lampiran 2B.','score',1,'Fasilitas Pedestrian:
1. Material perkerasan ………. (Permukaan stabil, kuat, Tahan Cuaca, Tekstur halus & tdk licin, tdk ada gundukan).
2. Kemiringan …….. (<1:8)
3. Penerangan jalan ……… (50 - 150 Lux)
4. Drainase ………….. 
5. Lebar ………………. (1,2 - 1,6 M)
6. Kanstin / Low Curb',false,NOW(),NOW(),'system'),
(108, 5, 'ASD 4 - 1','Adanya tempat parkir sepeda yang aman sebanyak 1 unit parkir per 20 pengguna gedung, hingga maksimal 100 unit parkir sepeda.','score',1,'Jumlah okupan tetap : …………..
Jumlah Rack Min : ……………..
Office  = Karyawan
mal = Karyawan
RS = Karyawan & Dokter tetap
Hotel = Karyawan
Apartemen = Karyawan
Sekolah = Karyawan + Dosen
Pabrik = Karyawan',false,NOW(),NOW(),'system'),
(109, 50, 'ASD 4 - 2','Apabila tolok ukur 1 di atas terpenuhi, perlu tersedianya shower sebanyak 1 unit untuk setiap 10 parkir sepeda. ','score',1,'Jumlah Shower Min : ……………
',false,NOW(),NOW(),'system'),
(110, 51, 'ASD 5 -1A','Adanya area lansekap berupa vegetasi (softscape) yang bebas dari bangunan taman (hardscape) yang terletak di atas permukaan tanah seluas minimal 40% luas total lahan. Luas area yang diperhitungkan adalah termasuk yang tersebut di Prasyarat 1, taman di atas basement, roof garden, terrace garden, dan wall garden, sesuai dengan Permen PU No. 5/PRT/M/2008 mengenai Ruang Terbuka Hijau (RTH) Pasal 2.3.1  tentang Kriteria Vegetasi untuk Pekarangan.','score',1,'Luas Area : ………………
Luas RTH  : …………………
Persentase : ……………',false,NOW(),NOW(),'system'),
(111, 51, 'ASD 5 -1B','Bila tolok ukur 1 dipenuhi, setiap penambahan 5% area lansekap dari luas total lahan mendapat 1 poin.','score',1,'Luas Area : ………………
Luas RTH  : …………………
Persentase : ……………',false,NOW(),NOW(),'system'),
(112, 51, 'ASD 5 -2','Penggunaan tanaman yang telah dibudidayakan secara lokal dalam skala provinsi, sebesar 60% luas  tajuk  dewasa terhadap luas areal lansekap pada ASD 5 tolok ukur 1.','score',1,'Luas RTH  : …………………
Luas Tajuk Tanaman Lokal : …………………..
Persentase : ……………',false,NOW(),NOW(),'system'),
(113, 52, 'ASD 6 - 1A','Menggunakan berbagai material untuk menghindari efek heat island pada area atap gedung sehingga nilai albedo (daya refleksi panas matahari) minimum 0,3 sesuai dengan perhitungan','score',1,'Jenis material finishing atap & nilai albedonya  :
1 .……………
2 .……………
3 .……………',false,NOW(),NOW(),'system'),
(114, 52, 'ASD 6 - 1B','Menggunakan green roof sebesar 50% dari luas atap yang tidak digunakan untuk mechanical electrical (ME), dihitung dari luas tajuk','score',1,'',false,NOW(),NOW(),'system'),
(115, 52, 'ASD 6 - 2','Menggunakan berbagai material untuk menghindari efek heat island pada area non-atap sehingga nilai albedo (daya refleksi panas matahari) minimum 0,3 sesuai dengan perhitungan','score',1,'Jenis material RT non hijau  & nilai albedonya :
1 .……………
2 .……………
3 .……………',false,NOW(),NOW(),'system'),
(116, 52, 'ASD 6 - 3A','Desain lansekap berupa vegetasi (softscape) pada sirkulasi utama pejalan kaki menunjukkan adanya pelindung dari panas akibat radiasi matahari.','score',1,'Pelindung panas  :
Kanopi Pohon / Pergola / ………………',false,NOW(),NOW(),'system'),
(117, 52, 'ASD 6 - 3B','Desain lansekap berupa vegetasi (softscape) pada sirkulasi utama pejalan kaki menunjukkan adanya pelindung dari terpaan angin kencang.','score',1,'Pelindung angin dari :
Hedge Semak / Hardscape……….. / ……………
Panjang Formasi Pelindung : …………….',false,NOW(),NOW(),'system'),
(118, 53, 'ASD 7 - 1A','Pengurangan beban volume limpasan air hujan ke jaringan drainase kota dari lokasi bangunan hingga 50 %, yang dihitung menggunakan intensitas curah hujan*.','score',1,'Cara yang digunakan:
1. ………….
2……………
Kapasitas total : …………………',false,NOW(),NOW(),'system'),
(119, 53, 'ASD 7 - 1B','Pengurangan beban volume limpasan air hujan ke jaringan drainase kota dari lokasi bangunan hingga 85 %, yang dihitung menggunakan nilai intensitas curah hujan*.  ','score',2,'Cara yang digunakan:
1. ………….
2……………
Kapasitas total : …………………',false,NOW(),NOW(),'system'),
(120, 53, 'ASD 7 - 2','Menunjukkan adanya upaya penanganan pengurangan beban banjir lingkungan dari luar lokasi bangunan','score',1,'Kapasitas total - 100% beban banjir : …………………',false,NOW(),NOW(),'system'),
(121, 53, 'ASD 7 - 3','Menggunakan teknologi-teknologi yang dapat mengurangi debit limpasan air hujan','score',1,'Teknologi yang digunakan:
1. ………….
2……………',false,NOW(),NOW(),'system'),
(122, 54, 'EEC P1 - 1','Memasang  kWh meter untuk mengukur konsumsi listrik pada setiap kelompok beban dan sistem peralatan, yang meliputi: ','prequisite',null,'BAS : Ya/Tidak
A. Bila menggunakan BAS: Dapat membaca konsumsi energi sistemnyang diminta secara terpisah/ Spesifikasi tidak untuk membaca konsumsi energi yang diminta
B. Bila tidak terbaca :
Letak Submeter : ……………..(deskripsi)
Jenis Kegiatan : ……………….
Jenis ruangan di gedung :
1 .……………
2 .……………
3 .…………...
Sub Meter Exclude (bila perlu): ………………',false,NOW(),NOW(),'system'),
(123, 55, 'EEC P2 - 1','Menghitung dengan cara perhitungan OTTV berdasarkan SNI 03-6389-2011 atau SNI edisi terbaru tentang  Konservasi Energi Selubung Bangunan pada Bangunan Gedung.','prequisite',null,'Nilai OTTV : …………….
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
(124, 56, 'EEC 1-1','Menggunakan Energy modelling software untuk menghitung konsumsi energi di gedung baseline dan gedung designed. Selisih konsumsi energi dari gedung baseline dan designed merupakan penghematan. Untuk setiap penghematan sebesar 2,5%, yang  dimulai dari penurunan energi sebesar 5% dari gedung baseline, mendapat 1 nilai (wajib untuk platinum).','max_score',20,'IKE Desain : ………
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
(125, 56, 'EEC 1-2','Menggunakan perhitungan worksheet, setiap penghematan 2% dari selisih antara gedung designed dan baseline mendapat nilai 1 poin. Penghematan mulai dihitung dari penurunan energi sebesar 5% dari gedung baseline. Worksheet yang dimaksud disediakan oleh GBCI.','max_score',20,'IKE Desain : ………
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
(126, 56, 'EEC 1-3-1-1','Nilai OTTV sesuai dengan SNI 03-6389-2011 atau SNI edisi terbaru tentang konservasi Energi Selubung Bangunan pada Bangunan Gedung.','score',3,'',false,NOW(),NOW(),'system'),
(127, 56, 'EEC 1-3-1-2','Apabila tolok ukur 1 dipenuhi, penurunan per 2.5W/m2mendapat 1 nilai sampai maksimal 2 nilai. ','score',2,'',false,NOW(),NOW(),'system'),
(128, 56, 'EEC 1-3-2-1','Menggunakan lampu dengan daya pencahayaan sebesar 15%, yang lebih hemat daripada daya pencahayaan yang tercantum dalam SNI 03 6197-2011','score',1,'Jenis lampu yang digunakan:
1 .……………
2 .……………
3 .…………...
Total Lighting Allowance : ……………..W/m2',false,NOW(),NOW(),'system'),
(129, 56, 'EEC 1-3-2-2','Menggunakan 100% ballast frekuensi tinggi (elektronik) untuk ruang kerja','score',1,'Jenis lampu indoor : ……………..',false,NOW(),NOW(),'system'),
(130, 56, 'EEC 1-3-2-3','Zonasi pencahayaan untuk seluruh ruang kerja yang dikaitkan dengan sensor gerak (motion sensor)','score',1,'Software Simulasi Pencahayaan : ……………………….
Letak Sensor : …………………..',false,NOW(),NOW(),'system'),
(131, 56, 'EEC 1-3-2-4','Penempatan tombol lampu dalam jarak pencapaian tangan pada saat buka pintu','score',1,'Deskripsi letak : ……………………………..',false,NOW(),NOW(),'system'),
(132, 56, 'ECC 1-3-3-1','Lift menggunakan traffic management system yang sudah lulus traffic analysis atau menggunakan regenerative drive system','score',1,'Ya / Tidak',false,NOW(),NOW(),'system'),
(133, 56, 'ECC 1-3-3-2','Menggunakan fitur hemat energi pada lift, menggunakan sensor gerak, atau sleep mode pada eskalator','score',1,'',false,NOW(),NOW(),'system'),
(134, 56, 'ECC 1-3-4 COP','Menggunakan peralatan Air Conditioning (AC) dengan COP minimum 10% lebih besar dari standar SNI 03-6390-2011 atau SNI edisi terbaru tentang Konservasi Energi pada Sistem Tata Udara Bangunan Gedung.','score',2,'Jenis Sistem AC : ……………
Jenis Chiller     : …………..
Nilai COP Plant   : ………………',false,NOW(),NOW(),'system'),
(135, 57 , 'EEC 2-1','Penggunaaan cahaya alami secara optimal sehingga minimal 30% luas lantai yang digunakan untuk bekerja mendapatkan intensitas cahaya alami minimal sebesar 300 lux. Perhitungan dapat dilakukan dengan cara manual atau dengan  software. ','score',2,'Software Simulasi Pencahayaan : ……………………….',false,NOW(),NOW(),'system'),
(136, 57, 'EEC 2-2','Jika butir satu dipenuhi lalu ditambah dengan adanya lux sensor untuk otomatisasi pencahayaan buatan apabila intensitas cahaya alami kurang dari 300 lux, didapatkan tambahan nilai 2 poin','score',2,'Letak Sensor : ……………..m dari perimeter
',false,NOW(),NOW(),'system'),
(137, 58, 'ECC 3','Tidak mengkondisikan (tidak memberi AC) ruang WC, tangga, koridor, dan lobi lift, serta melengkapi ruangan tersebut dengan ventilasi alami ataupun mekanik.','score',1,'Ventilasi Alami/Mekanik
Cara Intake:………….
Letak Intake : ……..
Cara Exhaust : …………….
Letak Exhaust : …….',false,NOW(),NOW(),'system'),
(138, 59, 'ECC 4','Menyerahkan perhitungan pengurangan emisi CO2 yang didapatkan dari selisih kebutuhan energi antara design building dan base building dengan menggunakan grid emission factor (konversi antara CO2 dan energi listrik) yang telah ditetapkan dalam Keputusan DNA pada B/277/Dep.III/LH/01/2009','score',1,'Cara perhitungan energi:
 Opsi 1 (Simulasi)/Opsi 2 (Worksheet)',false,NOW(),NOW(),'system'),
(139, 60, 'ECC 5','Menggunakan sumber energi baru dan terbarukan. Setiap 0,5% daya listrik yang dibutuhkan gedung yang dapat dipenuhi oleh sumber energi terbarukan mendapatkan 1 poin (sampai maksimal 5 poin).','max_score',5,'Generator Renewable : …………..
KWP total : …………..',false,NOW(),NOW(),'system'),
(140, 61, 'WAC P','Pemasangan alat meteran air (volume meter) yang ditempatkan di lokasi-lokasi tertentu pada sistem distribusi air, sebagai berikut:','prequisite',null,'Sumber Air Primer : ………………
Ada Fasilitas Recycling dari Grey Water  : ada/tdk
Ada Sumber Air Primer Alternatif :
ada/tdk',false,NOW(),NOW(),'system'),
(141, 61, 'WAC P2','Mengisi worksheet  air standar GBC Indonesia yang telah disediakan','prequisite',null,'Ada perhitungan dengan Water Calculator : ada / tdk',false,NOW(),NOW(),'system'),
(142, 62, 'WAC 1-1','Konsumsi air bersih dengan jumlah tertinggi 80% dari sumber primer tanpa mengurangi jumlah kebutuhan per orang sesuai dengan SNI 03-7065-2005 seperti pada tabel terlampir.','score',1,'Jumlah Okupan : …………….
Kebutuhan air :………………
Persentase : ………………..',false,NOW(),NOW(),'system'),
(143, 62, 'WAC 1-2','Setiap penurunan konsumsi air bersih dari sumber primer sebesar 5% sesuai dengan acuan pada poin 1 akan  mendapatkan nilai 1 dengan dengan nilai maksimum sebesar  7 poin.','max_score',7,'',false,NOW(),NOW(),'system'),
(144, 63, 'WAC 2-1A','Penggunaan water fixture yang sesuai dengan kapasitas buangan di bawah standar maksimum kemampuan alat keluaran air sesuai dengan lampiran, sejumlah minimal 25% dari total pengadaan produk water fixture. ','score',1,'Persentase WF Hemat : …………..
Type WF hemat:
1. .……………
2. .……………
3. .……………
4. .……………
5. .……………',false,NOW(),NOW(),'system'),
(145, 63, 'WAC 2-1B','Penggunaan water fixture yang sesuai dengan kapasitas buangan di bawah standar maksimum kemampuan alat keluaran air sesuai dengan lampiran, sejumlah minimal 50% dari total pengadaan produk water fixture. ','score',2,'',false,NOW(),NOW(),'system'),
(146, 63, 'WAC 2-1C','Penggunaan water fixture yang sesuai dengan kapasitas buangan di bawah standar maksimum kemampuan alat keluaran air sesuai dengan lampiran, sejumlah minimal 75% dari total pengadaan produk water fixture. ','score',3,'',false,NOW(),NOW(),'system'),
(147, 64, 'WAC 3-1A','Penggunaan seluruh air bekas pakai (grey water) yang telah di daur ulang untuk kebutuhan sistem flushing atau cooling tower.','score',2,'Penggunaan air recycling :
1. Flushing
2. Irigasi
3. Cooling Tower',false,NOW(),NOW(),'system'),
(148, 64, 'WAC 3 - 1B','Penggunaan seluruh air bekas pakai (grey water) yang telah didaur ulang untuk kebutuhan sistem flushing dan cooling tower - 3 nilai (hanya untuk water cooled)','score',3,'',false,NOW(),NOW(),'system'),
(149, 65, 'WAC 4-1A','Menggunakan salah satu dari tiga alternatif sebagai berikut: air kondensasi AC, air bekas wudu, atau air hujan.','score',1,'Sumber air Alternative : ………………',false,NOW(),NOW(),'system'),
(150, 65, 'WAC 4-1B','Menggunakan lebih dari satu sumber air dari ketiga alternatif di atas. ','score',2,'Sumber air Alternative : ………………',false,NOW(),NOW(),'system'),
(151, 65, 'WAC 4-1C','Menggunakan teknologi yang memanfaatkan air laut atau air danau atau air sungai untuk keperluan air bersih sebagai sanitasi, irigasi dan kebutuhan lainnya','score',2,'Sumber air Alternative : ………………',false,NOW(),NOW(),'system'),
(152, 66, 'WAC 5-1A','Menyediakan instalasi tangki  penampungan air hujan kapasitas  50% dari jumlah air hujan yang jatuh di atas atap bangunan yang dihitung menggunakan nilai intensitas curah hujan harian rata-rata 10 tahunan setempat.','score',1,'Kapasitas Tangki : ………………….
Persentase penyimpanan : ………….',false,NOW(),NOW(),'system'),
(153, 66, 'WAC 5-1B','Menyediakan Instalasi tangki  penampungan air hujan kapasitas 75%  dari perhitungan di atas.','score',2,'',false,NOW(),NOW(),'system'),
(154, 66, 'WAC 5-1C','Menyediakan Instalasi tangki penyimpanan penampungan air hujan kapasitas 100 % dari perhitungan di atas.','score',3,'',false,NOW(),NOW(),'system'),
(155, 67, 'WAC 6-1','Seluruh air yang digunakan untuk irigasi gedung tidak berasal dari sumber air tanah dan/atau PDAM. ','score',1,'Sumber air irigasi :
1……………….
2……………….',false,NOW(),NOW(),'system'),
(156, 67, 'WAC 6-2','Menerapkan teknologi yang inovatif untuk irigasi yang dapat mengontrol kebutuhan air untuk lansekap yang tepat, sesuai dengan kebutuhan tanaman.','score',1,'Teknologi yang digunakan:
1. ………….
2……………',false,NOW(),NOW(),'system'),
(157, 68, 'MRC P','Tidak menggunakan chloro fluoro carbon (CFC) sebagai refrigeran dan halon sebagai bahan pemadam kebakaran','prequisite',null,'Jenis Refrigeran :………………….
Jenis bahan Fire Fighting: ……………',false,NOW(),NOW(),'system'),
(158, 69, 'MRC 1-1A','Menggunakan kembali semua material bekas, baik dari bangunan lama maupun tempat lain, berupa bahan struktur utama, fasad, plafon, lantai, partisi, kusen, dan dinding, setara minimal 10% dari total biaya material.','score',1,'',false,NOW(),NOW(),'system'), 
(159, 69, 'MRC 1-1B','Menggunakan kembali semua material bekas, baik dari bangunan lama maupun tempat lain, berupa bahan struktur utama, fasad, plafon, lantai, partisi, kusen, dan dinding, setara minimal 20% dari total biaya material.','score',2,'',false,NOW(),NOW(),'system'),
(160, 70, 'MRC 2-1','Menggunakan material yang memiliki sertifikat sistem manajemen lingkungan pada proses produksinya minimal bernilai 30% dari total biaya material. Sertifikat dinilai sah bila masih berlaku dalam rentang waktu proses pembelian dalam konstruksi berjalan. ','score',1,'',false,NOW(),NOW(),'system'),
(161, 70, 'MRC 2-2','Menggunakan material yang merupakan hasil proses daur ulang minimal bernilai 5% dari total biaya material. ','score',1,'',false,NOW(),NOW(),'system'),
(162, 70, 'MRC 2-3','Menggunakan material yang bahan baku utamanya berasal dari sumber daya (SD) terbarukan dengan masa panen jangka pendek (<10 tahun) minimal bernilai 2% dari total biaya material.','score',1,'',false,NOW(),NOW(),'system'),
(163, 71, 'MRC 3','Tidak menggunakan bahan perusak ozon pada seluruh sistem pendingin gedung','score',2,'Jenis Refrigeran :………………….',false,NOW(),NOW(),'system'),
(164, 72, 'MRC 4-1','Menggunakan bahan material kayu yang bersertifikat legal sesuai dengan Peraturan Pemerintah tentang asal kayu (seperti faktur angkutan kayu olahan/FAKO, sertifikat perusahaan, dan lain-lain) dan sah terbebas dari perdagangan kayu ilegal sebesar 100% biaya total material kayu','score',1,'',false,NOW(),NOW(),'system'),
(165, 72, 'MRC 4-2','Jika 30% dari butir di atas menggunakan kayu bersertifikasi dari pihak Lembaga Ekolabel Indonesia (LEI) atau Forest Stewardship Council (FSC)','score',1,'',false,NOW(),NOW(),'system'),
(166, 73, 'MRC 5','Desain yang menggunakan material modular atau prafabrikasi (tidak termasuk equipment) sebesar 30% dari total biaya material','score',3,'',false,NOW(),NOW(),'system'),
(167, 74, 'MRC 6-1','Menggunakan material yang lokasi asal bahan baku utama dan pabrikasinya berada di dalam radius 1.000 km dari lokasi proyek minimal bernilai 50% dari total biaya material. ','score',1,'',false,NOW(),NOW(),'system'),
(168, 74, 'MRC 6-2','Menggunakan material yang lokasi asal bahan baku utama dan pabrikasinya berada dalam wilayah Republik Indonesia bernilai minimal 80% dari total biaya material. ','score',1,'',false,NOW(),NOW(),'system'),
(169, 75, 'IHC P','Desain ruangan yang menunjukkan adanya potensi introduksi udara luar minimal sesuai dengan Standar ASHRAE 62.1-2007 atau Standar ASHRAE edisi terbaru.','prequisite',null,'Perhitungan : ada/tidak
Standar acuan yg digunakan : …………………..',false,NOW(),NOW(),'system'),
(170, 76, 'IHC 1','Ruangan dengan kepadatan tinggi, yaitu < 2.3m2 per orang dilengkapi dengan instalasi sensor gas karbon dioksida (CO2) yang memiliki mekanisme untuk mengatur jumlah ventilasi udara luar sehingga konsentrasi C02 di dalam ruangan tidak lebih dari 1.000 ppm, sensor diletakkan 1,5 m di atas lantai dekat return air gril atau return air duct.','score',1,'Ruangan yang dipasang CO2 monitor: 
1. ………………
2.………………
3.………………',false,NOW(),NOW(),'system'),
(171, 77, 'IHC 2','Memasang tanda “Dilarang Merokok di Seluruh Area Gedung” dan tidak menyediakan bangunan/area khusus untuk merokok di dalam gedung. Apabila tersedia, bangunan/area merokok di luar gedung, minimal berada pada jarak  5 m dari pintu masuk, outdoor air intake, dan bukaan jendela.','score',2,'',false,NOW(),NOW(),'system'),
(172, 78, 'IHC 3-1','Menggunakan cat dan coating yang mengandung kadar volatile organic compounds (VOCs) rendah, yang ditandai dengan label/sertifikasi yang diakui GBC Indonesia','score',1,'',false,NOW(),NOW(),'system'),
(173, 78, 'IHC 3-2','Menggunakan produk kayu komposit dan laminating adhesive, dengan syarat  memiliki kadar emisi formaldehida rendah, yang ditandai dengan label/sertifikasi yang diakui GBC Indonesia','score',1,'',false,NOW(),NOW(),'system'),
(174, 78, 'IHC 3-3','Menggunakan material lampu yang kandungan merkurinya pada toleransi maksimum yang disetujui GBC Indonesia dan tidak menggunakan material yang mengandung asbestos.','score',1,'',false,NOW(),NOW(),'system'),
(175, 79, 'IHC 4','Apabila 75% dari net lettable area (NLA) menghadap langsung ke pemandangan luar yang dibatasi bukaan transparan bila ditarik suatu garis lurus','score',1,'Mungkin/ Tidak
Persentase NLA : ………………',false,NOW(),NOW(),'system'),
(176, 80, 'IHC 5','Menggunakan lampu dengan iluminansi (tingkat pencahayaan) ruangan sesuai dengan SNI 03-6197-2011  Tentang Konservasi Energi pada Sistem Pencahayaan.','score',1,'',false,NOW(),NOW(),'system'),
(177, 81, 'IHC 6','Menetapkan perencanaan kondisi termal ruangan secara umum pada suhu 250C dan kelembaban relatif 60%','score',1,'Ya / Tidak
Jenis AC:
(bila bukan central) Mekanisme: ………………………………………………',false,NOW(),NOW(),'system'),
(178, 82, 'IHC 7','Tingkat kebisingan pada 90% dari nett lettable area (NLA) tidak lebih dari atau sesuai dengan SNI 03-6386-2000,  tentang Spesifikasi Tingkat Bunyi dan waktu Dengung dalam Bangunan Gedung dan Perumahan (kriteria desain yang direkomendasikan).','score',1,'',false,NOW(),NOW(),'system'),
(179, 83, 'BEM P','Adanya instalasi atau fasilitas untuk memilah dan mengumpulkan sampah sejenis sampah rumah tangga (UU No. 18 Tahun 2008) berdasarkan jenis organik, anorganik dan B3','prequisite',null,'Jenis Fasilitas : …………….
Di luar / dlm gedung
Di luar / dlm area
Mandiri / pihak ke-tiga',false,NOW(),NOW(),'system'),
(180, 84, 'BEM 1','Melibatkan minimal seorang tenaga ahli yang sudah bersertifikat GREENSHIP Professional (GP), yang bertugas untuk  memandu proyek  hingga mendapatkan  sertifikat GREENSHIP.','score',1,'Nama/Perusahaan : ……………',false,NOW(),NOW(),'system'),
(181, 85, 'BEM 2-1','Limbah padat, dengan menyediakan area pengumpulan, pemisahan, dan sistem pencatatan. Pencatatan dibedakan berdasarkan limbah padat yang dibuang ke TPA, digunakan kembali, dan didaur ulang oleh pihak ketiga.','score',1,'',false,NOW(),NOW(),'system'),
(182, 85, 'BEM 2-2','Limbah cair, dengan menjaga kualitas seluruh buangan air yang timbul dari aktivitas konstruksi agar tidak mencemari drainase kota','score',1,'',false,NOW(),NOW(),'system'),
(183, 86, 'BEM 3-1','Mengolah limbah organik  gedung   yang dilakukan secara mandiri maupun bekerja sama dengan pihak ketiga , sehingga menambah nilai manfaat dan dapat mengurangi dampak lingkungan.','score',1,'Metode :……………
Mandiri / pihak ke-tiga
Di luar / dlm gedung
Di luar / dlm area',false,NOW(),NOW(),'system'),
(184, 86, 'BEM 3-2','Mengolah limbah an-organik gedung  yang dilakukan secara mandiri maupun bekerja sama dengan pihak ketiga, sehingga menambah nilai manfaat dan dapat mengurangi dampak lingkungan.','score',1,'Metode :……………
Mandiri / pihak ke-tiga
Di luar / dlm gedung
Di luar / dlm area
',false,NOW(),NOW(),'system'),
(185, 87, 'BEM 4-1','Melakukan prosedur testing- commissioning sesuai dengan petunjuk GBC Indonesia, termasuk pelatihan terkait untuk optimalisasi kesesuaian fungsi dan kinerja peralatan/sistem dengan perencanaan dan acuannya.','score',2,'
Metode yg digunakan: …………..
Pihak ke-3 yg terlibat : …………..
Jadwal selesai pemasangan AC :
Jadwal TC :………….',false,NOW(),NOW(),'system'),
(186, 87, 'BEM 4-2','Memastikan seluruh measuring adjusting instrument telah terpasang pada saat konstruksi dan memperhatikan kesesuaian antara desain dan spesifikasi teknis terkait komponen propper commissioning.','score',1,'Pihak ke-3 terlibat sejak tahap : ……….',false,NOW(),NOW(),'system'),
(187, 88, 'BEM 5-1','Menyerahkan data implementasi green building sesuai dengan form dari GBC Indonesia.','score',1,'',false,NOW(),NOW(),'system'),
(188, 88, 'BEM 5-2','Memberi pernyataan bahwa pemilik gedung akan menyerahkan data implementasi green building dari bangunannya dalam waktu 12 bulan setelah tanggal sertifikasi kepada GBC Indonesia dan suatu pusat data energi Indonesia yang akan ditentukan kemudian','score',1,'',false,NOW(),NOW(),'system'),
(189, 89, 'BEM 6','Memiliki surat perjanjian dengan penyewa gedung (tenant) untuk gedung yang disewakan atau SPO untuk gedung yang digunakan sendiri, yang terdiri atas:','score',1,'',false,NOW(),NOW(),'system'),
(190, 90, 'BEM 7','Memberi pernyataan bahwa pemilik gedung akan mengadakan survei suhu dan kelembaban paling lambat 12 bulan setelah tanggal sertifikasi dan menyerahkan laporan hasil survei paling lambat 15 bulan setelah tanggal sertifikasi kepada GBC Indonesia.','score',2,'',false,NOW(),NOW(),'system');

INSERT INTO `master_documents`
VALUES
  (134, 96, 'ASD P1','Perhitungan yang menunjukan persentase area lansekap berupa vegetasi (softscape).',NOW(),NOW(),'system'),
  (135, 96, 'ASD P1','Gambar for construction atau gambar as built lansekap yang memuat informasi area dasar h  ijau.',NOW(),NOW(),'system'),
  (136, 96, 'ASD P1','Gambar as built potongan tapak lahan proyek yang dapat menunjukkan posisi basement*.',NOW(),NOW(),'system'),
  (137, 96, 'ASD P1','Gambar as built denah tapak yang menunjukkan garis batas basement*.',NOW(),NOW(),'system'),
  (138, 96, 'ASD P1','(*) Apabila gedung tidak memiliki basement, dokumen ini dapat menjadi bukti bahwa proyek tidak memiliki basement atau fasilitas bawah tanah.',NOW(),NOW(),'system'),
  (139, 97, 'ASD P2','Perhitungan yang menunjukan komposisi vegetasi.',NOW(),NOW(),'system'),
  (140, 97, 'ASD P2','Gambar for construction lansekap yang memuat informasi mengenai formasi vegetasi.',NOW(),NOW(),'system'),
  (141, 97, 'ASD P2','Daftar vegetasi yang digunakan pada lahan mencakup informasi jenis tanaman, luas tajuk dan fungsinya, yang sesuai dengan:
  o Komposisi vegetasi mengikuti Peraturan Menteri Dalam Negeri  No.1 tahun 2007 pasal 13 (2a) 
  o Vegetasi memiliki kriteria berdasarkan Peraturan Menteri Pekerjaan Umum No. 5/PRT/M/2008',NOW(),NOW(),'system'),
  (142, 98, 'ASD 1-1A','Peta lokasi / gambar yang menunjukkan adanya prasarana dan sarana pada tolok ukur.',NOW(),NOW(),'system'),
  (143, 98, 'ASD 1-1A','',NOW(),NOW(),'system'),
  (144, 99, 'ASD 1-1B','Dokumen yang menunjukkan pembangunan dilakukan pada lahan peruntukan dengan KLB > 3.',NOW(),NOW(),'system'),
  (145, 99, 'ASD 1-1B','',NOW(),NOW(),'system'),
  (146, 100, 'ASD 1-2','Laporan pelaksanaan revitalisasi, yang dilengkapi data berupa:
  o Penjelasan dan bukti metode revitalisasi yang dilakukan
  o Foto lokasi pra dan pasca pembangunan
  o Gambar as built revitalisasi area yang dimaksud
  o Jika menyangkut revitalisasi area relatif kumuh maka perlu disampaikan juga bukti metode revitalisasi dan persetujuan komunitas yang terkena program revitalisasi area tersebut yang representatif dan sah secara hukum',NOW(),NOW(),'system'),
  (147, 101, 'ASD 2-1','Peta lokasi yang menunjukkan lokasi fasilitas umum berikut dengan jarak tempuhnya. ',NOW(),NOW(),'system'),
  (148, 102, 'ASD 2-2','Peta lokasi yang menunjukkan rencana sirkulasi akses pejalan kaki dan lokasi fasilitas umum berikut dengan jarak tempuhnya.',NOW(),NOW(),'system'),
  (149, 102, 'ASD 2-2','Gambar as built tapak yang menunjukkan akses pejalan kaki yang dibuka untuk menuju fasilitas umum.',NOW(),NOW(),'system'),
  (150, 103, 'ASD 2 - 3','Peta lokasi yang menunjukkan fasilitas/akses bebas dari perpotongan kendaraan bermotor dan lokasi  fasilitas umum.',NOW(),NOW(),'system'),
  (151, 103, 'ASD 2 - 3','Gambar as built tapak yang menunjukkan fasilitas/akses bebas dari perpotongan kendaraan bermotor.',NOW(),NOW(),'system'),
  (152, 104, 'ASD 2 - 4','Surat pernyataan dari pemilik gedung akan membuka lantai dasar gedung untuk akses pejalan kaki selama 10 jam/hari.',NOW(),NOW(),'system'),
  (153, 104, 'ASD 2 - 4','Gambar as built yang menunjukkan lantai dasar gedung yang dibuka untuk akses pejalan kaki.',NOW(),NOW(),'system'),
  (154, 105, 'ASD 3 - 1A','Peta lokasi yang menunjukkan letak halte atau stasiun transportasi umum dari gerbang lokasi bangunan, berikut dengan jarak tempuhnya.',NOW(),NOW(),'system'),
  (155, 105, 'ASD 3 - 1A','Informasi trayek transportasi umum yang tersedia.',NOW(),NOW(),'system'),
  (156, 105, 'ASD 3 - 1A','Foto halte atau stasiun transportasi umum yang dimaksud dalam penilaian',NOW(),NOW(),'system'),
  (157, 106, 'ASD 3 - 1B','Bukti sewa atau kepemilikan shuttle bus.',NOW(),NOW(),'system'),
  (158, 106, 'ASD 3 - 1B','Daftar trayek untuk shuttle bus yang disediakan.',NOW(),NOW(),'system'),
  (159, 106, 'ASD 3 - 1B','Foto shuttle bus yang dimaksud dalam penilaian',NOW(),NOW(),'system'),
  (160, 107, 'ASD 3 - 2','Gambar as built desain jalur pedestrian.',NOW(),NOW(),'system'),
  (161, 107, 'ASD 3 - 2','Gambar as built  tapak yang menunjukan letak jalur pedestrian.',NOW(),NOW(),'system'),
  (162, 108, 'ASD 4 - 1','Gambar as built perletakan tempat parkir sepeda.',NOW(),NOW(),'system'),
  (163, 108, 'ASD 4 - 1','SPB atau gambar as built yang menunjukkan desain/ tipe tempat parkir sepeda yang digunakan.',NOW(),NOW(),'system'),
  (164, 108, 'ASD 4 - 1','Bukti fotografis rak sepeda yang telah dipasang.',NOW(),NOW(),'system'),
  (165, 109, 'ASD 4 - 2','Gambar as built perletakan shower.',NOW(),NOW(),'system'),
  (166, 109, 'ASD 4 - 2','Gambar as built desain shower untuk pengguna sepeda.',NOW(),NOW(),'system'),
  (167, 109, 'ASD 4 - 2','Bukti fotografis shower untuk pengguna sepeda.',NOW(),NOW(),'system'),
  (168, 110, 'ASD 5 -1A','Perhitungan yang menunjukan persentase area lansekap berupa vegetasi (softscape) .',NOW(),NOW(),'system'),
  (169, 110, 'ASD 5 -1A','Gambar for construction lansekap yang memuat informasi mengenai formasi vegetasi.',NOW(),NOW(),'system'),
  (170, 111, 'ASD 5 -1B','Perhitungan yang menunjukan persentase area lansekap berupa vegetasi (softscape).  ',NOW(),NOW(),'system'),
  (171, 111, 'ASD 5 -1B','Gambar for construction lansekap yang memuat informasi mengenai formasi vegetasi. ',NOW(),NOW(),'system'),
  (172, 112, 'ASD 5 -2','Perhitungan yang menunjukan total luas tajuk tanaman lokal dan budidaya lokal.',NOW(),NOW(),'system'),
  (173, 112, 'ASD 5 -2','Daftar vegetasi yang digunakan mencakup informasi jenis tanaman, luas tajuk serta asal dan tempat budi dayanya.',NOW(),NOW(),'system'),
  (174, 113, 'ASD 6 - 1','Perhitungan albedo atap keseluruhan beserta sumber nilai albedonya.',NOW(),NOW(),'system'),
  (175, 113, 'ASD 6 - 1','Gambar as built atap yang menunjukkan material atap yang digunakan.',NOW(),NOW(),'system'),
  (176, 113, 'ASD 6 - 1','SPB material atap yang digunakan.',NOW(),NOW(),'system'),
  (177, 113, 'ASD 6 - 1','Foto atap gedung yang menunjukkan material atap terkait penilaian tolok ukur 1A.',NOW(),NOW(),'system'),
  (178, 114, 'ASD 6 - 1B','Perhitungan luas green roof.',NOW(),NOW(),'system'),
  (179, 114, 'ASD 6 - 1B','Gambar as built atap yang menunjukkan green roof.',NOW(),NOW(),'system'),
  (180, 115, 'ASD 6 - 2','Perhitungan albedo perkerasan non-atap keseluruhan beserta sumber nilai albedonya.',NOW(),NOW(),'system'),
  (181, 115, 'ASD 6 - 2','Gambar as built non-atap yang menunjukkan material perkerasan yang digunakan.',NOW(),NOW(),'system'),
  (182, 115, 'ASD 6 - 2','SPB material perkerasan non-atap yang digunakan.',NOW(),NOW(),'system'),
  (183, 115, 'ASD 6 - 2','Foto area perkerasan non-atap yang menunjukkan material perkerasan terkait penilaian tolok ukur 2.',NOW(),NOW(),'system'),
  (184, 116, 'ASD 6 - 3A','Laporan singkat yang menjelaskan aplikasi desain pelindung dari panas akibat radiasi matahari yang direncanakan, mencakup:',NOW(),NOW(),'system'),
  (185, 116, 'ASD 6 - 3A','o Penjelasan pengaruh aplikasi desain terhadap sirkulasi utama pejalan kaki (bisa berupa skema, simulasi dan lain sebagainya)',NOW(),NOW(),'system'),
  (186, 116, 'ASD 6 - 3A','o Gambar as built jalur pedestrian yang menunjukkan adanya pelindung dari panas akibat radiasi matahari pada sirkulasi utama pejalan kaki',NOW(),NOW(),'system'),
  (187, 116, 'ASD 6 - 3A','o Foto penerapan aplikasi desain di bangunan',NOW(),NOW(),'system'),
  (188, 117, 'ASD 6 - 3B','Laporan singkat yang menjelaskan aplikasi desain pelindung dari terpaan angin kencang yang direncanakan, mencakup:',NOW(),NOW(),'system'),
  (189, 117, 'ASD 6 - 3B','o Penjelasan pengaruh aplikasi desain terhadap sirkulasi utama pejalan kaki (bisa berupa skema, simulasi dan lain sebagainya)',NOW(),NOW(),'system'),
  (190, 117, 'ASD 6 - 3B','o Gambar as built jalur pedestrian yang menunjukkan adanya pelindung dari terpaan angin kencang pada sirkulasi utama pejalan kaki.',NOW(),NOW(),'system'),
  (191, 117, 'ASD 6 - 3B','o Foto penerapan aplikasi desain di bangunan',NOW(),NOW(),'system'),
  (192, 118, 'ASD 7 - 1A','Perhitungan volume air hujan yang ditangani dalam lahan proyek. ',NOW(),NOW(),'system'),
  (193, 118, 'ASD 7 - 1A','Skema penanganan air hujan meliputi: arah aliran air, proses pengolahan beserta pemanfaatannya.',NOW(),NOW(),'system'),
  (194, 118, 'ASD 7 - 1A','Gambar as built fasilitas penanganan air hujan.',NOW(),NOW(),'system'),  
  (195, 119, 'ASD 7 - 1B','Perhitungan volume air hujan yang ditangani dalam lahan proyek. ',NOW(),NOW(),'system'),
  (196, 119, 'ASD 7 - 1B','Skema penanganan air hujan meliputi: arah aliran air, proses pengolahan beserta pemanfaatannya.',NOW(),NOW(),'system'),
  (197, 119, 'ASD 7 - 1B','Gambar as built fasilitas penanganan air hujan.',NOW(),NOW(),'system'),
  (198, 120, 'ASD 7 - 2','Perhitungan volume air hujan yang menunjukan penanganan dari luar lokasi bangunan yang ditangani.',NOW(),NOW(),'system'),
  (199, 120, 'ASD 7 - 2','Skema penanganan beban banjir lingkungan dari luar lokasi bangunan meliputi: arah aliran air, proses pengolahan beserta pemanfaatannya.',NOW(),NOW(),'system'),
  (200, 120, 'ASD 7 - 2','Gambar as built fasilitas penanganan beban banjir lingkungan dari luar lokasi lahan proyek.',NOW(),NOW(),'system'),
  (201, 121, 'ASD 7 - 3','Gambar as built teknologi pengurangan debit limpasan air hujan.',NOW(),NOW(),'system'),
  (202, 121, 'ASD 7 - 3','Gambar as built letak penempatan teknologi pengurangan debit limpasan air hujan.',NOW(),NOW(),'system'),
  (203, 121, 'ASD 7 - 3','Laporan penanganan, meliputi: skema penanganan debit air hujan, deskripsi cara kerjanya dan pengaruhnya terhadap usaha mengurangi debit limpasan air hujan.',NOW(),NOW(),'system'),
  (204, 122, 'EEC P1','Gambar as built skema distribusi listrik satu garis yang menunjukkan lokasi-lokasi sub meter dan pemisahan beban sesuai dengan tolok ukur',NOW(),NOW(),'system'),
  (205, 122, 'EEC P1','Bukti fotografis sub meter yang telah dipasang',NOW(),NOW(),'system'),
  (206, 123, 'EEC P2','Dokumen Perhitungan Desain Selimut Bangunan berdasarkan SNI 03-6389-2000 tentang Konservasi Energi Selubung Bangunan pada Bangunan Gedung',NOW(),NOW(),'system'),
  (207, 124, 'EEC 1-1','Dokumen perhitungan konsumsi energi gedung yang menggunakan energy modelling software berdasarkan spesifikasi desain yang tertuang dalam dokumen as built.',NOW(),NOW(),'system'),
  (208, 125, 'EEC 1-2','Dokumen perhitungan konsumsi energi gedung yang menggunakan worksheet GBC Indonesia berdasarkan spesifikasi desain yang tertuang dalam dokumen as built.',NOW(),NOW(),'system'),
  (209, 126, 'EEC 1-3','Dokumen yang dibutuhkan tiap tolok ukur pada kriteria EEC 1-3 dirincikan sebagai berikut :',NOW(),NOW(),'system'),
  (210, 127, 'EEC 1-3-1','Perhitungan OTTV gedung dan selisihnya dengan nilai 35 watt/m2 berdasarkan spesifikasi material selimut bangunan yang ada dalam dokumen tender',NOW(),NOW(),'system'),
  (211, 128, 'EEC 1-3-2-1','Gambar as built rencana ME lampu yang menunjukkan lampu beserta spesifikasi daya yang digunakan ',NOW(),NOW(),'system'),
  (212, 128, 'EEC 1-3-2-1','Perhitungan daya per luasan berdasarkan spesifikasi daya pada lampu yang digunakan dalam dokumen as built',NOW(),NOW(),'system'),
  (213, 129, 'EEC 1-3-2-2','Gambar as built rencana ME lampu yang menunjukkan lampu beserta spesifikasi daya yang digunakan ',NOW(),NOW(),'system'),
  (214, 129, 'EEC 1-3-2-2','Spesifikasi  lampu yang memiliki ballast elektronik',NOW(),NOW(),'system'),
  (215, 130, 'EEC 1-3-2-3','Gambar as built ME lampu yang menunjukkan peletakan sensor gerak.',NOW(),NOW(),'system'),
  (216, 131, 'EEC 1-3-2-4','Gambar as built ME lampu yang menunjukkan peletakan saklar.',NOW(),NOW(),'system'),
  (217, 132, 'EEC 1-3-3','Spesifikasi elevator yang digunakan memiliki traffic analysis atau menggunakan regenerative drive system atau eskalator yang menggunakan sleep mode.',NOW(),NOW(),'system'),
  (218, 133, 'EEC 1-3-3','Surat pengantar Barang atau dokumen lain yang setara untuk menunujukkan bukti pembelian elevator atau eskalator yang dimaksud',NOW(),NOW(),'system'),
  (219, 134, 'EEC 1-3-4','Spesifikasi sistem pendingin dengan COP di atas SNI atau standar lainnya.',NOW(),NOW(),'system'),
  (220, 134, 'EEC 1-3-4','Surat pengantar Barang atau dokumen lain yang setara untuk menunujukkan bukti pembelian sistem pendingin yang dimaksud',NOW(),NOW(),'system'),
  (221, 135, 'EEC 2-1','Dokumen as built denah yang menunjukkan lokasi bukaan transparan',NOW(),NOW(),'system'),
  (222, 135, 'EEC 2-1','Dokumen as built tampak yang menunjukkan sisi bukaan',NOW(),NOW(),'system'),
  (223, 135, 'EEC 2-1','Dokumen as built detail yang menunjukkan detail bukaan transparan (opsional atau hanya disiapkan jika tim verifikasi GBCI memerlukan dokumen tersebut)',NOW(),NOW(),'system'),
  (224, 135, 'EEC 2-1','Pengukuran langsung di lapangan sesuai dengan metode yang ditetapkan oleh ',NOW(),NOW(),'system'),
  (225, 136, 'EEC 2-2','Gambar as built yang menunjukkan zonasi pencahayaan',NOW(),NOW(),'system'),
  (226, 136, 'EEC 2-2','Dokumen spesifikasi lux sensor',NOW(),NOW(),'system'),
  (227, 137, 'EEC 3','Gambar as built denah yang menunjukkan lokasi yang tidak menggunakan AC beserta jenis ventilasinya',NOW(),NOW(),'system'),
  (228, 137, 'EEC 3','Gambar as built yang menunjukkan detail ventilasi alami',NOW(),NOW(),'system'),
  (229, 137, 'EEC 3','Dokumen spesifikasi teknis ventilasi mekanik yang digunakan',NOW(),NOW(),'system'),
  (230, 137, 'EEC 3','Surat Pengantar Barang (SPB) ventilasi mekanik yang digunakan',NOW(),NOW(),'system'),
  (231, 138, 'EEC 4','Dokumen Perhitungan CCI berdasarkan dokumen laporan pelaksanaan',NOW(),NOW(),'system'),
  (232, 139, 'EEC 5','Dokumen gambar as built yang menunjukkan lokasi energi terbarukan pada gedung',NOW(),NOW(),'system'),
  (233, 139, 'EEC 5','Perhitungan penghematan yang dicapai melalui energi terbarukan',NOW(),NOW(),'system'),
  (234, 139, 'EEC 5','Dokumen spesifikasi teknologi energi terbarukan',NOW(),NOW(),'system'),
  (235, 139, 'EEC 5','SPB teknologi energi terbarukan',NOW(),NOW(),'system'),
  (236, 140, 'WAC P','Gambar tender skema distribusi air satu garis yang menunjukkan pemisahan sesuai dengan tolok ukur dan lokasi sub meter',NOW(),NOW(),'system'),
  (237, 140, 'WAC P','Bukti fotografis sub meter yang telah dipasang',NOW(),NOW(),'system'),
  (238, 141, 'WAC P2','Water Calculation',NOW(),NOW(),'system'),
  (239, 141, 'WAC P2','Perhitungan worksheet sesuai dengan spesifikasi dalam dokumen as built',NOW(),NOW(),'system'),
  (240, 142, 'WAC 1-1','Perhitungan penggunaan air sesuai dengan spesifikasi dalam dokumen as built',NOW(),NOW(),'system'),
  (241, 143, 'WAC 1-2','Perhitungan penggunaan air sesuai dengan spesifikasi dalam dokumen as built',NOW(),NOW(),'system'),
  (242, 144, 'WAC 2-1A','Spesifikasi water fixture yang digunakan ',NOW(),NOW(),'system'),
  (243, 145, 'WAC 2-1B','Perhitungan kuantitas water fixture  sesuai dengan spesifikasi dalam dokumen as built',NOW(),NOW(),'system'),
  (244, 146, 'WAC 2-1C','Surat pengantar barang (SPB) water fixture yang digunakan',NOW(),NOW(),'system'),
  (245, 147, 'WAC 3-1A','Gambar as built fasilitas daur ulang air',NOW(),NOW(),'system'),
  (246, 147, 'WAC 3-1A','Dokumen  spesifikasi instalasi daur ulang air dan kualitas air hasil pengolahan',NOW(),NOW(),'system'),
  (247, 148, 'WAC 3 - 1B','Ringkasan eksekutif pelaksanaan testing – komisioning untuk sistem air daur ulang. ',NOW(),NOW(),'system'),
  (248, 148, 'WAC 3 - 1B','Bukti fotografis pipa air hasil daur ulang',NOW(),NOW(),'system'),
  (249, 149, 'WAC 4-1A','Gambar as built yang menunjukkan instalasi pengambilan air alternatif dan distribusinya',NOW(),NOW(),'system'),
  (250, 150, 'WAC 4-1B','Gambar as built yang menunjukkan instalasi pengambilan air alternatif dan distribusinya',NOW(),NOW(),'system'),
  (251, 151, 'WAC 4-1C','Gambar as built yang menunjukkan instalasi pengambilan air alternatif dan distribusinya',NOW(),NOW(),'system'),
  (252, 152, 'WAC 5-1A','Perhitungan kapasitas penampungan air hujan berdasarkan dokumen as built',NOW(),NOW(),'system'),
  (253, 152, 'WAC 5-1A','Gambar as built penampungan air hujan',NOW(),NOW(),'system'),
  (254, 153, 'WAC 5-1B','Skema pengambilan, penampungan, dan pemanfaatan air hujan',NOW(),NOW(),'system'),
  (255, 154, 'WAC 5-1C','Kapasitas 50 % = 1 Poin, Kapasitas 75 % = 2 Poin, Kapasitas 100 % = 3 Poin',NOW(),NOW(),'system'),
  (256, 155, 'WAC 6-1','Perhitungan konsumsi air untuk irigasi berdasarkan kalkulator WAC sesuai dengan dokumen as built ',NOW(),NOW(),'system'),
  (257, 155, 'WAC 6-1','Skema rencana irigasi tanaman meliputi (sumber air, cara penyiraman)',NOW(),NOW(),'system'),
  (258, 155, 'WAC 6-1','Gambar as bulit area yang menggunakan irigasi pada gedung',NOW(),NOW(),'system'),
  (259, 156, 'WAC 6-2','Dokumen spesifikasi teknologi irigasi yang sesuai dengan kebutuhan tanaman',NOW(),NOW(),'system'),
  (260, 156, 'WAC 6-2','SPB water fixture yang digunakan',NOW(),NOW(),'system'),
  (261, 157, 'MRC P','Dokumen spesifikasi produk yang menunjukkan bahan refrigeran dan bahan pemadam kebakaran yang digunakan.',NOW(),NOW(),'system'),
  (262, 157, 'MRC P','Surat Pengantar Barang yang menunjukkan tipe dan kuantitas pembelian produk sistem pendingin dan bahan pemadam kebakaran.',NOW(),NOW(),'system'),
  (263, 158, 'MRC 1-1A','Gambar as built yang menggambarkan bagian dan volume material bekas yang digunakan.',NOW(),NOW(),'system'),
  (264, 158, 'MRC 1-1A','Bukti fotografis yang menunjukkan lokasi penggunaan material bekas on site  pada saat sebelum, sedang dan sesudah aktivitas konstruksi berlangsung.',NOW(),NOW(),'system'),
  (265, 158, 'MRC 1-1A','Dokumen perhitungan berdasarkan format yang telah disediakan.',NOW(),NOW(),'system'),
  (266, 159, 'MRC 1-1B','Surat Pengantar Barang yang menunjukkan pembelian material bekas off site untuk digunakan pada gedung proyek.',NOW(),NOW(),'system'),
  (267, 160, 'MRC 2-1','Dokumen perhitungan material yang proses produksinya memiliki sistem manajemen lingkungan berdasarkan format yang telah disediakan.',NOW(),NOW(),'system'),
  (268, 160, 'MRC 2-1','Salinan sertifikat SML dari lembaga independen yang masih berlaku.',NOW(),NOW(),'system'),
  (269, 160, 'MRC 2-1','Surat Pengantar Barang yang menunjukkan tipe dan kuantitas pembelian material yang proses produksinya memiliki sistem manajemen lingkungan.',NOW(),NOW(),'system'),
  (270, 161, 'MRC 2-2','Dokumen perhitungan material yang berasal dari proses daur ulang berdasarkan format yang telah disediakan.',NOW(),NOW(),'system'),
  (271, 161, 'MRC 2-2','Spesifikasi teknis dari pihak produsen yang menunjukkan bawa material berasal dari proses daur ulang.',NOW(),NOW(),'system'),
  (272, 161, 'MRC 2-2','Surat Pengantar Barang yang menunjukkan tipe dan kuantitas pembelian material yang berasal dari proses daur ulang.',NOW(),NOW(),'system'),
  (273, 162, 'MRC 2-3','Dokumen perhitungan material yang berasal dari sumber terbarukan dengan masa panen <10 tahun berdasarkan format yang telah disediakan.',NOW(),NOW(),'system'),
  (274, 162, 'MRC 2-3','Spesifikasi teknis dari pihak produsen yang menunjukkan bawa material yang berasal dari sumber terbarukan dengan masa panen <10 tahun.',NOW(),NOW(),'system'),
  (275, 162, 'MRC 2-3','Surat Pengantar Barang yang menunjukkan tipe dan kuantitas pembelian material yang berasal dari sumber terbarukan dengan masa panen <10 tahun.',NOW(),NOW(),'system'),
  (276, 163, 'MRC 3','Dokumen spesifikasi produk yang menyatakan sistem pendingin tanpa kandungan bahan yang memiliki potensi merusak ozon, atau ODP=0.',NOW(),NOW(),'system'),
  (277, 163, 'MRC 3','SPB yang menunjukkan tipe dan kuantitas pembelian produk sistem pendingin tanpa kandungan bahan yang memiliki potensi merusak ozon, atau ODP=0.',NOW(),NOW(),'system'),
  (278, 164, 'MRC 4-1','Dokumen gambar as built yang menunjukkan lokasi penggunaan material kayu secara keseluruhan',NOW(),NOW(),'system'),
  (279, 164, 'MRC 4-1','Dokumen perhitungan material kayu berdasarkan format yang telah disediakan.',NOW(),NOW(),'system'),
  (280, 164, 'MRC 4-1','Dokumen FAKO/FAKB seluruh material kayu yang digunakan dalam proyek dari pihak vendor.',NOW(),NOW(),'system'),
  (281, 164, 'MRC 4-1','Surat Pengantar Barang yang menunjukkan tipe dan kuantitas pembelian material kayu secara keseluruhan.',NOW(),NOW(),'system'),
  (282, 165, 'MRC 4-2','Dokumen gambar as built yang menunjukkan lokasi penggunaan material kayu yang memiliki sertifikat LEI/FSC.',NOW(),NOW(),'system'),
  (283, 165, 'MRC 4-2','Dokumen perhitungan material kayu berdasarkan format yang telah disediakan.',NOW(),NOW(),'system'),
  (284, 165, 'MRC 4-2','Dokumen sertifikat LEI/FSC dari material kayu yang digunakan dalam proyek.',NOW(),NOW(),'system'),
  (285, 165, 'MRC 4-2','Surat Pengantar Barang yang menunjukkan tipe dan kuantitas pembelian material kayu yang memiliki sertifikat LEI/FSC.',NOW(),NOW(),'system'),
  (286, 166, 'MRC 5','Dokumen perhitungan biaya material pra fabrikasi yang formatnya ditetapkan oleh GBC Indonesia',NOW(),NOW(),'system'),
  (287, 166, 'MRC 5','Gambar as Built yang menunjukkan posisi material pra fabrikasi yang digunakan',NOW(),NOW(),'system'),
  (288, 166, 'MRC 5','Dokumen yang menunjukan spesifikasi dari pihak produsen material prefabrikasi yang digunakan dalam proyek.',NOW(),NOW(),'system'),
  (289, 166, 'MRC 5','Surat Pengantar Barang atau dokumen lain yang menujukan kuantitas material pra fabrikasi yang digunakan dalam proyek.',NOW(),NOW(),'system'),
  (290, 167, 'MRC 6-1','Dokumen perhitungan material lokal yang asal bahan baku dan lokasi manufaktur berjarak 1000 km dari lokasi proyek berdasarkan format yang telah disediakan.',NOW(),NOW(),'system'),
  (291, 168, 'MRC 6-2','Dokumen perhitungan material lokal yang asal bahan baku dan lokasi manufakturnya berada dalam wilayah Republik Indonesia.',NOW(),NOW(),'system'),
  (292, 169, 'IHC P','Perhitungan yang menunjukkan jumlah introduksi udara luar sesuai dengan ASHRAE 62.1.2007 atau standar ASHRAE terbaru.',NOW(),NOW(),'system'),
  (293, 169, 'IHC P','Bukti fotografis fresh air intake gedung.',NOW(),NOW(),'system'),
  (294, 170, 'IHC 1','Gambar as built yang menunjukkan peletakan CO2 monitoring, antara lain dapat berupa: detail potongan, aksonometri',NOW(),NOW(),'system'),
  (295, 170, 'IHC 1','Deskripsi skema pengaturan CO2 monitoring',NOW(),NOW(),'system'),
  (296, 170, 'IHC 1','Dokumen spesifikasi CO2 monitoring',NOW(),NOW(),'system'),
  (297, 170, 'IHC 1','SPB CO2 Monitoring yang digunakan',NOW(),NOW(),'system'),
  (298, 171, 'IHC 2','Untuk gedung tanpa Area Merokok',NOW(),NOW(),'system'),
  (299, 171, 'IHC 2','Surat pernyataan dari pemilik gedung yang menerangkan bahwa ada larangan merokok di dalam gedung',NOW(),NOW(),'system'),
  (300, 171, 'IHC 2','Dokumen as built denah yang menunjukkan peletakkan tanda dilarang merokok di dalam gedung proyek',NOW(),NOW(),'system'),
  (301, 171, 'IHC 2','Bukti fotografis tanda dilarang merokok',NOW(),NOW(),'system'),
  (302, 171, 'IHC 2','Untuk Gedung dengan Area Merokok',NOW(),NOW(),'system'),
  (303, 171, 'IHC 2','Dokumen as built denah yang menunjukkan jarak area merokok terhadap pintu masuk, outdoor air intake, dan bukaan jendela (jika ada)',NOW(),NOW(),'system'),
  (304, 171, 'IHC 2','Bukti fotografis area merokok (jika ada).',NOW(),NOW(),'system'),
  (305, 172, 'IHC 3-1','Daftar seluruh produk cat dan coating yang digunakan (meliputi nama merek dagang, produsen, kuantitas, kadar VOC produk, standar VOC yang diacu) ',NOW(),NOW(),'system'),
  (306, 172, 'IHC 3-1','Dokumen spesifikasi produk yang menunjukkan kadar VOC, antara lain: MSDS, technical data sheet, sertifikat ramah lingkungan',NOW(),NOW(),'system'),
  (307, 172, 'IHC 3-1','SPB seluruh cat dan coating yang digunakan',NOW(),NOW(),'system'),
  (308, 173, 'IHC 3-2','Daftar seluruh produk kayu komposit dan agrifiber serta laminating adhesive yang digunakan (meliputi nama merek dagang, produsen, kuantitas, kadar emisi formaldehida atau VOC produk, dan standar yang diacu)',NOW(),NOW(),'system'),
  (309, 173, 'IHC 3-2','Dokumen spesifikasi produk yang menunjukkan kadar VOC/emisi formaldehida, antara lain: MSDS, technical data sheet, sertifikat produk ramah lingkungan',NOW(),NOW(),'system'),
  (310, 173, 'IHC 3-2','SPB seluruh kayu komposit dan agrifiber serta laminating adhesive yang digunakan',NOW(),NOW(),'system'),
  (311, 174, 'IHC 3-3','Daftar seluruh produk, meliputi: ',NOW(),NOW(),'system'),
  (312, 174, 'IHC 3-3','Produk lampu (merek dagang, jumlah unit, data spesifik kadar merkuri, dan referensi standar yang diijinkan)',NOW(),NOW(),'system'),
  (313, 174, 'IHC 3-3','Material untuk plafon, atap, dan insulasi (merek dagang dan jumlah unit)',NOW(),NOW(),'system'),
  (314, 174, 'IHC 3-3','Dokumen spesifikasi seluruh produk lampu, material plafon dan atap, serta material insulasi yang digunakan',NOW(),NOW(),'system'),
  (315, 174, 'IHC 3-3','SPB seluruh produk lampu, material plafon dan atap, serta material insulasi',NOW(),NOW(),'system'),
  (316, 175, 'IHC 4','Dokumen as built denah yang menunjukkan peletakan bukaan transparan',NOW(),NOW(),'system'),
  (317, 175, 'IHC 4','Dokumen as built detail bukaan transparan',NOW(),NOW(),'system'),
  (318, 175, 'IHC 4','Perhitungan persentase luas ruang aktif yang mendapat pemandangan luar secara langsung',NOW(),NOW(),'system'),
  (319, 176, 'IHC 5','Laporan pengukuran pencahayaan buatan di lapangan sesuai dengan metode yang ditetapkan GBC Indonesia',NOW(),NOW(),'system'),
  (320, 176, 'IHC 5','Denah ruangan yang menunjukkan titik lokasi pengukuran',NOW(),NOW(),'system'),
  (321, 177, 'IHC 6','Dokumen yang menunjukkan bahwa input data dalam perhitungan cooling load menggunakan suhu dan kelembaban relatif sesuai tolok ukur.',NOW(),NOW(),'system'),
  (322, 178, 'IHC 7','Laporan pengukuran tingkat kebisingan di lapangan  sesuai dengan metode yang ditetapkan GBC Indonesia',NOW(),NOW(),'system'),
  (323, 178, 'IHC 7','Denah ruangan yang menunjukkan titik lokasi pengukuran',NOW(),NOW(),'system'),
  (324, 179, 'BEM P','Surat pernyataan bahwa pemilik gedung akan melakukan pemisahan sampah berdasarkan organik, anorganik, dan B3 dari dalam gedung sampai keluar lahan gedung.',NOW(),NOW(),'system'),
  (325, 179, 'BEM P','Dokumen POS yang menunjukkan mekanisme pemisahan dan pengangkutan sampah dari dalam gedung ke luar lahan gedung.',NOW(),NOW(),'system'),
  (326, 179, 'BEM P','Gambar as built yang menunjukkan peletakan fasilitas sampah terpisah dalam gedung dan di dalam lahan gedung.',NOW(),NOW(),'system'),
  (327, 180, 'BEM 1','Surat penunjukkan GP yang terlibat yang ditandatangani oleh pemilik proyek.',NOW(),NOW(),'system'),
  (328, 180, 'BEM 1','Fotokopi sertifikat GP yang terlibat dalam proyek.',NOW(),NOW(),'system'),
  (329, 180, 'BEM 1','Laporan absensi GP dalam setiap rapat koordinasi proyek yang ditandatangani pemilik proyek.',NOW(),NOW(),'system'),
  (330, 181, 'BEM 2-1','Dokumen laporan pelaksanaan manajemen limbah padat saat konstruksi.',NOW(),NOW(),'system'),
  (331, 181, 'BEM 2-1','Surat pernyataan kerja sama dengan pihak ketiga terkait usaha manajemen sampah yang dapat didaur ulang atau digunakan kembali.',NOW(),NOW(),'system'),
  (332, 181, 'BEM 2-1','Bukti fotografis area pemilahan limbah padat saat konstruksi.',NOW(),NOW(),'system'),
  (333, 182, 'BEM 2-2','Dokumen laporan pelaksanaan manajemen limbah cair saat konstruksi.',NOW(),NOW(),'system'),
  (334, 182, 'BEM 2-2','Bukti fotografis pengelolaan limbah cair saat konstruksi.',NOW(),NOW(),'system'),
  (335, 183, 'BEM 3-1','Jika pengelolaan dilakukan oleh pihak ketiga: Surat perjanjian kerja sama dengan pihak ketiga untuk melakukan pengolahan limbah organik.',NOW(),NOW(),'system'),
  (336, 183, 'BEM 3-1','Jika pengolahan dilakukan secara mandiri: Gambar as built yang menunjukkan instalasi pengomposan.',NOW(),NOW(),'system'),
  (337, 183, 'BEM 3-1','Bukti fotografis instalasi pengomposan.',NOW(),NOW(),'system'),
  (338, 184, 'BEM 3-2','Jika  pengelolaan dilakukan oleh pihak ketiga: Surat perjanjian kerja sama dengan pihak ketiga untuk melakukan pengolahan limbah anorganik.',NOW(),NOW(),'system'),
  (339, 184, 'BEM 3-2','Jika pengolahan dilakukan secara mandiri: Gambar as built yang menunjukkan instalasi pengolahan.',NOW(),NOW(),'system'),
  (340, 184, 'BEM 3-2','Bukti fotografis instalasi pengolahan.',NOW(),NOW(),'system'),
  (341, 185, 'BEM 4-1','Salinan laporan hasil pelaksanaan meliputi: hasil uji kerja peralatan yang terpasang dibanding dengan yang direncanakan, termasuk executive summary bila terdapat perbedaan antara kapasitas AC dengan desain.',NOW(),NOW(),'system'),
  (342, 185, 'BEM 4-1','Salinan jadwal pelaksanaan training komisioning yang dilengkapi dengan nama penanggung jawab, pelaksana, dan pengawas komisioning',NOW(),NOW(),'system'),
  (343, 186, 'BEM 4-2','Gambar as built mekanikal elektrikal yang menunjukkan letak pemasangan peralatan measuring adjusting instrument.',NOW(),NOW(),'system'),
  (344, 186, 'BEM 4-2','Bukti fotografis pemasangan peralatan measuring adjusting instrument',NOW(),NOW(),'system'),
  (345, 187, 'BEM 5-1','Perhitungan perbandingan persentase kenaikan investasi pembangunan green building terhadap pembangunan gedung konvensional.',NOW(),NOW(),'system'),
  (346, 188, 'BEM 5-2','Surat pernyataan bahwa pemilik gedung akan menyerahkan data kepada GBC Indonesia setelah 1 tahun dari tanggal penerimaan sertifikasi, yang meliputi: data konsumsi energi, data konsumsi air, data volume sampah (organik dan anorganik).',NOW(),NOW(),'system'),
  (347, 188, 'BEM 5-2','Setelah 1 tahun dari tanggal penerimaan sertifikasi, diperlukan data konsumsi energi, data konsumsi air, data volume sampah (organik dan anorganik).',NOW(),NOW(),'system'),
  (348, 189, 'BEM 6','Untuk gedung yang disewakan: Draft / Dokumen perjanjian sewa (lease agreement) dari pihak pemilik gedung bahwa penyewa gedung (tenant) akan: ',NOW(),NOW(),'system'),
  (349, 189, 'BEM 6','Menggunakan kayu yang bersertifikat untuk material fit out.',NOW(),NOW(),'system'),
  (350, 189, 'BEM 6','Mengikuti pelatihan yang akan dilakukan oleh manajemen gedung.',NOW(),NOW(),'system'),
  (351, 189, 'BEM 6','Melakukan manajemen indoor air quality (IAQ) setelah konstruksi fit out selesai',NOW(),NOW(),'system'),
  (352, 189, 'BEM 6','Untuk gedung yang digunakan sendiri: Surat pernyataan  bahwa pemilik  gedung akan:',NOW(),NOW(),'system'),
  (353, 189, 'BEM 6','Menggunakan kayu yang bersertifikat untuk material fit out.',NOW(),NOW(),'system'),
  (354, 189, 'BEM 6','Melaksanakan pelatihan untuk seluruh staf yang akan dilakukan oleh manajemen gedung.',NOW(),NOW(),'system'),
  (355, 189, 'BEM 6','Melakukan rencana manajemen indoor air quality (IAQ) setelah konstruksi fit out selesai. Implementasi dalam bentuk  SPO.',NOW(),NOW(),'system'),
  (356, 190, 'BEM 7','Surat pernyataan bahwa pemilik gedung akan melaksanakan survei suhu dan kelembaban selambat-lambatnya 12 bulan setelah tanggal sertifikasi dan menyerahkan laporan hasil survei 15 bulan setelah tanggal sertifikasi kepada GBC Indonesia.',NOW(),NOW(),'system');

INSERT INTO `master_criteria_blockers`
VALUES
(71, 98, 99, NOW(), NOW(),'system'),
(72, 99, 98, NOW(), NOW(),'system'),
(73, 105, 106, NOW(), NOW(),'system'),
(74, 106, 105, NOW(), NOW(),'system'),
(75, 113, 114 , NOW(), NOW(),'system'),
(76, 114, 113 , NOW(), NOW(),'system'),
(77, 116, 117 , NOW(), NOW(),'system'),
(78, 117, 116 , NOW(), NOW(),'system'),
(79, 118, 119 , NOW(), NOW(),'system'),
(80, 119, 118 , NOW(), NOW(),'system'),
(81, 144, 145 , NOW(), NOW(),'system'),
(82, 144, 146 , NOW(), NOW(),'system'),
(83, 145, 144 , NOW(), NOW(),'system'),
(84, 145, 146 , NOW(), NOW(),'system'),
(85, 146, 144 , NOW(), NOW(),'system'),
(86, 146, 145 , NOW(), NOW(),'system'),
(87, 147, 148 , NOW(), NOW(),'system'),
(88, 148, 147 , NOW(), NOW(),'system'),
(89, 149, 150 , NOW(), NOW(),'system'),
(91, 150, 149 , NOW(), NOW(),'system'),
(92, 150, 151 , NOW(), NOW(),'system'),
(90, 149, 151 , NOW(), NOW(),'system'),
(93, 151, 149 , NOW(), NOW(),'system'),
(94, 151, 150 , NOW(), NOW(),'system'),
(95, 152, 153 , NOW(), NOW(),'system'),
(96, 152, 154 , NOW(), NOW(),'system'),
(97, 153, 152 , NOW(), NOW(),'system'),
(98, 153, 154 , NOW(), NOW(),'system'),
(99, 154, 152 , NOW(), NOW(),'system'),
(100, 154, 153 , NOW(), NOW(),'system'),
(101, 158, 159 , NOW(), NOW(),'system'),
(102, 159, 158 , NOW(), NOW(),'system'),
(103, 124, 125	, NOW(), NOW(),'system'),
(104, 124, 126	, NOW(), NOW(),'system'),
(105, 124, 127	, NOW(), NOW(),'system'),
(106, 124, 128	, NOW(), NOW(),'system'),
(107, 124, 129	, NOW(), NOW(),'system'),
(108, 124, 130	, NOW(), NOW(),'system'),
(109, 124, 134	, NOW(), NOW(),'system'),
(110, 124, 132	, NOW(), NOW(),'system'),
(111, 124, 133	, NOW(), NOW(),'system'),
(112, 124, 131	, NOW(), NOW(),'system'),
(113, 125, 124	, NOW(), NOW(),'system'),
(114, 125, 126	, NOW(), NOW(),'system'),
(115, 125, 127	, NOW(), NOW(),'system'),
(116, 125, 130	, NOW(), NOW(),'system'),
(117, 125, 133	, NOW(), NOW(),'system'),
(118, 125, 132	, NOW(), NOW(),'system'),
(119, 125, 129	, NOW(), NOW(),'system'),
(120, 125, 128	, NOW(), NOW(),'system'),
(121, 125, 131	, NOW(), NOW(),'system'),
(122, 125, 134	, NOW(), NOW(),'system'),
(123, 126, 124	, NOW(), NOW(),'system'),
(124, 126, 125	, NOW(), NOW(),'system'),
(125, 127, 125	, NOW(), NOW(),'system'),
(126, 127, 124	, NOW(), NOW(),'system'),
(127, 128, 124	, NOW(), NOW(),'system'),
(128, 128, 125	, NOW(), NOW(),'system'),
(129, 129, 124	, NOW(), NOW(),'system'),
(130, 129, 125	, NOW(), NOW(),'system'),
(131, 130, 124	, NOW(), NOW(),'system'),
(132, 130, 125	, NOW(), NOW(),'system'),
(133, 131, 124	, NOW(), NOW(),'system'),
(134, 131, 125	, NOW(), NOW(),'system'),
(135, 132, 124	, NOW(), NOW(),'system'),
(136, 132, 125	, NOW(), NOW(),'system'),
(137, 133, 124	, NOW(), NOW(),'system'),
(138, 133, 125	, NOW(), NOW(),'system'),
(139, 134, 124	, NOW(), NOW(),'system'),
(140, 134, 125	, NOW(), NOW(),'system');
