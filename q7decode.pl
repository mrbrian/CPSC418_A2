my $str = "MNPBP NAWGB XJIWU IWZZF GJIWR ILYYO EJPOE YTTVO CJFAE RJYXX 
XAJKW ZXSWV WJTAO MLOIE WRFBD CHDXM XNPXG RDYUE IXPBF NATPE 
GNYNV RYNWX QTTZF NJIYO SHIHW XMNGH RANGY CSZPE REYYO EJIWR 
QAZVS GLOIE VOWGX NDJWB XLYME RJIVM VATBD YYJYY KSPDD LTPAR 
NTZZF GATMG ZCGTO WMPXI RATMG ZCGCO JYZPE LNQHM SGYZF GAWXG 
EENMG IBPBM WESXV XIBXS ATQHE GOYNR IPGQX ATSIS QYNWK IWQZE 
XFQAX CYLWG QYQYS BERGG RDOAJ ZKQAE GYNXS TCQBS XXJVI KGQYW 
WDBYO GJIWS WMQYC DYNID CHMGI IJYAO SDGLF GNPDD PLPQF MWQLO 
EHGXM GYTZD MNJYF EJAVG WXIWM ZCDXM XASQM VTGWX QTTHE XJIXH 
XGRGG JEVWF GJPNG IKPXI GJPWV ZXJEG ICSQE LAEKF GYVGY CSQMV 
ZMPXS MNPEF AGPHH XYSHW XPSOD JYGLS ZXYAO CYYDV IXSLZ IXPBH 
QENWM ZXJHW XMNVM OXQTF PPSVK IWMGK ZJQOV ZBNVR ZXRPO GYDPE 
XFWGI XDRWG XWOQQ XSRVS QKNWR WDPYO BDYDS ATWXV GMQYM BACWH 
ZDOAW XWPWF ALEZD MNZZO DEPEO NDNXS TDQVX GNYTD CHQQG ZBZXR 
AETBS QYWXG BTZPW WDCGO RNQAX VAEEE GEMWL SDOAE GDRVS QOYNR 
ZDPKF GYTPG SHCWX MNPJD ALZGW WDNVQ QJCVQ KLPQF CSSNX KYJPD 
VJIWL ZWZWI RYNBM VTGWH WSPAE RNOBY KLOAE GDRXM MNPBS SBVGK 
PYCWI RTSXI ZHPGK ZBVGG RAWOE FYQNS NJIWG XCQBM IRPHW WXCPE 
AAGQD LAWGY MNOBV ZMAGK WJZPE ZXZVA SYQYC BNOAE RATPE AYQKW 
XSVGG ZXYHW XWMNQ WJRXM ZWFBM WASUD PTZXG NKNGS QYTVM ZDPTE 
CBFAR MTYAK IWDWK XYJEF LFMXI WKFOF MENKF GYJVI KWFEL NKOAP 
OLQBS WMEGY ZWPHO IRFKW MNPXG MTTHE QYNYR ZDPYF MUCYY CJPQS 
QYTGY CSTWG YYJPD VATOF SHIHE ANPBR AAZKW XSIVM IVPYW ZXCGK 
BNOHE GNOYS XSWWV PORVS QJIWU WXAKV ZCEGY ZWPHW XANHD GJPGK 
MNPBV WHIHV NBFAI NSPXV";

my $hash0 = {
'W' => 'E',
'A' => 'T',
'X' => 'O',
'Y' => 'A',
'M' => 'N',
'G' => 'I',
'P' => 'R',
'O' => 'S',
'Q' => 'H',
'F' => 'D',
'S' => 'L',
'E' => 'U',
'V' => 'C',
'I' => 'M',
'L' => 'P',
'C' => 'F',
'B' => 'Y',
'K' => 'W',
'N' => 'G',
'T' => 'B',
'R' => 'V',
'D' => 'K',
'J' => 'J',
'Z' => 'X',
'H' => 'Z',
'U' => 'Q'};

my $hash1 = {
'W' => 'E',
'G' => 'T',
'X' => 'O',
'N' => 'A',
'Y' => 'N',
'M' => 'I',
'Z' => 'R',
'A' => 'S',
'E' => 'H',
'P' => 'D',
'D' => 'L',
'O' => 'U',
'S' => 'C',
'Q' => 'M',
'R' => 'P',
'H' => 'F',
'V' => 'Y',
'I' => 'W',
'F' => 'G',
'J' => 'B',
'L' => 'V',
'B' => 'K',
'C' => 'J',
'T' => 'X',
'K' => 'Z'};

my $hash2 = {
'X' => 'E',
'G' => 'T',
'Q' => 'O',
'I' => 'A',
'W' => 'N',
'P' => 'I',
'Y' => 'R',
'N' => 'S',
'M' => 'H',
'J' => 'D',
'D' => 'L',
'S' => 'U',
'E' => 'C',
'Z' => 'M',
'K' => 'P',
'T' => 'F',
'O' => 'Y',
'V' => 'W',
'B' => 'G',
'A' => 'B',
'R' => 'V',
'L' => 'K',
'H' => 'J',
'F' => 'X',
'C' => 'Z',
'U' => 'Q'};

my $hash3 = {
'Y' => 'E',
'I' => 'T',
'B' => 'O',
'A' => 'A',
'Q' => 'N',
'N' => 'I',
'T' => 'R',
'D' => 'S',
'G' => 'H',
'R' => 'D',
'Z' => 'L',
'V' => 'U',
'E' => 'C',
'K' => 'M',
'H' => 'P',
'J' => 'F',
'P' => 'Y',
'W' => 'W',
'M' => 'G',
'F' => 'B',
'X' => 'V',
'C' => 'K',
'S' => 'J',
'O' => 'X',
'L' => 'Z'};

my $hash4 = {
'Z' => 'E',
'Y' => 'T',
'G' => 'O',
'W' => 'A',
'S' => 'N',
'D' => 'I',
'N' => 'R',
'X' => 'S',
'P' => 'H',
'T' => 'D',
'E' => 'L',
'V' => 'U',
'M' => 'C',
'C' => 'M',
'R' => 'P',
'Q' => 'F',
'A' => 'Y',
'J' => 'W',
'H' => 'G',
'O' => 'B',
'F' => 'V',
'I' => 'K',
'B' => 'J',
'K' => 'X',
'U' => 'Z'};

my $shift0=0;
my $shift1=11;
my $shift2=20;
my $shift3=13;
my $shift4=12;

my $count = 0;
$str =~ s/ //g;
$str =~ s/\n//g;

for my $chr (split //, $str) {
    print "$hash0->{$chr}" if ($count % 5 == 0);
    print "$hash1->{$chr}" if ($count % 5 == 1);
    print "$hash2->{$chr}" if ($count % 5 == 2);
    print "$hash3->{$chr}" if ($count % 5 == 3);
    print "$hash4->{$chr} " if ($count % 5 == 4);
    $count += 1;
  }
  print \n;
=item

for my $chr (split //, $str) {
    print chr((ord($chr)-65+$shift0)%26+65) if ($count % 5 == 0);
    print chr((ord($chr)-65+$shift1)%26+65) if ($count % 5 == 1);
    print chr((ord($chr)-65+$shift2)%26+65) if ($count % 5 == 2);
    print chr((ord($chr)-65+$shift3)%26+65) if ($count % 5 == 3);
    print chr((ord($chr)-65+$shift4)%26+65) if ($count % 5 == 4);
    $count += 1;
  }
  print \n;
=cut
