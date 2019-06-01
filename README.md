# TicTacToe-ClientServer

Aplikasi ini merupakan tugas besar mata kuliah Pengantar Jaringan Komputer

Aplikasi ini merupakan sebuah game tradisional TicTacToe (Catur Jawa) berbasis client dan server

Aplikasi ini dibuat menggunakan netbeans, maka dari itu, folder-folder nya juga menyesuaikan netbeans agar dapat di run menggunakan netbeans

Program pada client dan server ini dibuat menjadi 1 pada file TicTacToe.java

Board TicTacToe dibuat menggunakan JFrame

# Kelas TicTacToe

Terdapat atribut array syaratMenang (berisi syarat menang sebuah permainan TicTacToe ditandai dengan indexnya)

Asumsi board TicTacToe selalu 3x3

0   1   2

3   4   5

6   7   8

    1. main() --> menjalankan projek TicTacToe
    
    2. TicTacToe() --> constructor program
    
    3. load() --> menampilkan board tictactoe polos
    
    4. run() --> menjalankan GUI
    
    5. tick() --> saat program dijalankan, akan di cek apakah bisa connect dengan server, apabila lebih dari 10x tidak bisa connect, maka koneksi akan terputus
    apabila bisa konek, maka permainan akan dimulai, dan akan terus di cek kemenangannya
    
    6. cekLawanMenang() --> mengecek apakah lawan kita sudah menang dengan mengecek atribut array syaratMenang
    
    7. cekMenang() --> mengecek apakah kita sudah menang dengan mengecek atribut array syaratMenang
    
    8. cekImbang() --> mengecek apakah permainan imbang, apabila tidak ada space untuk bermain lagi, maka bisa dipastikan permainan berakhir imbang
    
    9. request() --> menginisialisasi koneksi server (client request koneksi ke server)
    
    10. initialize() --> menginisialisasi suatu koneksi
    
    11. connect() --> memberitahukan bahwa client dan server sudah terkoneksi pada ip dan port tertentu
    
    12. render() --> berfungsi logika pemrograman untuk mengeset ukuran papan, membuat garis pada papan yang menang
    
# Kelas Painter
    
    1. Painter() --> constructor untuk  membuat board, dan bersedia menerima input mouse (addEventListener)
    
    2. paintComponent() --> berfungsi untuk memanggil method render() untuk membuat garis pada space tertentu

    3. mouseClicked() --> berisi logika pemrograman untuk mengetahui saat di klik, pin yang muncul X / O dan meng handle apabila ada player yang mengklik saat bukan gilirannya
    
    4. mousePressed() --> override
    
    5. mouseReleased() --> override
    
    6. mouseEntered() --> override
    
    7. mouseExited() --> override
