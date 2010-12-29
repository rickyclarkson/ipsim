#!/usr/bin/perl
$prefix="/var/www/home/netsim";
$rip=$ENV{"REMOTE_ADDR"};
$posted="";
srand(time^$$);

%wday=(0,"Sun",1,"Mon",2,"Tue",3,"Wed",4,"Thu",5,"Fri",6,"Sat",7,"Sun");
%mon=(1,"Jan",2,"Feb",3,"Mar",4,"Apr",5,"May",6,"Jun",7,"Jul",8,"Aug",9,"Sep",10,"Oct",11,"Nov",12,"Dec");
%lmon=(1,"January",2,"February",3,"March",4,"April",5,"May",6,"June",7,"July",8,"August",9,"September",10,"October",11,"November",12,"December");
($sec,$min,$hour,$mday,$mon,$year,$wday,$yday,$isdst)=localtime(time);
$year+=1900;
$maildate=sprintf("%s, %d %s %d %02d:%02d:%02d +0%d00",$wday{$wday},$mday,$mon{++$mon},$year,$hour,$min,$sec,$isdst?1:0);
  
if ($ENV{"REQUEST_METHOD"} eq "POST") {
  $posted=<STDIN>;
  $posted=~s/[\n\r]//g;
}

#Return codes
# 1xx = OK
# 2xx = Called wrongly
# 3xx = Deny
# 4xx = Fatal error

print "Content-type: text/html\n\n";
if ($posted eq "") {
  print "201: nothing to report\n";
}
elsif ($posted=~/([TM]*)GET (.*)/) {
  $mode=$1;
  $arg=$2;

  if ($mode eq "T") {
    $dir="testmode";
    if (!testmode()) {
      print "301: DENY\n";
      return;
    }
  }
  elsif ($mode eq "M") {
    $dir="markmode";
    if (!markmode()) {
      print "302: DENY\n";
      return;
    }
  }
  else {
    $dir="config";
  }

  if (open(FILE,"$prefix/$dir/$arg")) {
    print "101: OK\n";
    while (<FILE>) {
      print;
    }
    close FILE;
  }
  else {
    print "401: file $prefix/$dir/$arg not found\n"
  }
}
elsif ($posted=~/([TM]*)PUT (.*)/) {
  $mode=$1;
  $arg=$2;

  if ($mode eq "T") {
    $dir="testmode";
    if (!testmode()) {
      print "303: DENY\n";
      return;
    }
  }
  elsif ($mode eq "M") {
    $dir="markmode";
    if (!markmode()) {
      print "304: DENY\n";
      return;
    }
  }
  else {
    $dir="upload";
  }

  $s1="";$s2="";
  while (-e "$prefix/$dir/$arg$s1$s2") {
    $s1=".";$s2++;
  }
  if (open (FILE,">$prefix/$dir/$arg$s1$s2")) {
    while (<STDIN>) {
      print FILE;
    }
    close FILE;
    print "102: $arg$s1$s2\n";
  }
  else {
    print "402: file $prefix/$dir/$arg$s1$s2 not written\n"
  }
}
elsif ($posted=~/GETRANDOM (.*)/) {
  $arg=$1;
  $numfiles=0;
  if (opendir(DIR,"$prefix/config/$arg")) {
    while ($dir=readdir(DIR)) {
      next if $dir=~/^\./;
      $dirlist{$numfiles++}=$dir;
    }
    $rn=int(rand()*$numfiles);
    if (open(FILE,"$prefix/config/$arg/$dirlist{$rn}")) {
      print "103: OK\n";
      while (<FILE>) {
        print;
      }
      close FILE;
    }
    else {
      print "403: random file $prefix/config/$arg/$dirlist{$rn} not found\n"
    }
  }
  else {
    print "404: random dir $prefix/config/$arg not found\n"
  }
  closedir DIR;
}
elsif ($posted=~/MGETUS (.*)/) {
  $arg=$1;
  if (!markmode()) {
    print "305: DENY\n";
    return;
  }
  $rescode=105;
  $numfiles=0;
  if (opendir(DIR,"$prefix/testmode/$arg/solutions")) {
    while ($file=readdir(DIR)) {
      next if $file=~/^\./;
      $numfiles++;
      if (!(-e "$prefix/markmode/$arg/feedback/$file")) {
        $rescode=104;
        $restext=$file;
        last;
      }
    }
    if ($rescode==104) {
      if (open(FILE,"$prefix/testmode/$arg/solutions/$restext")) {
        print "104: $restext\n";
        while (<FILE>) {
          print;
        }
        close FILE;
      }
      else {
        print "406: solution file $prefix/textmode/$arg/solutions/$restext not found\n"
      }
    }
    elsif ($rescode==105) {
      print "105: Finished ($numfiles)\n";
    }
    else {
      print "204: Unknown\n";
    }
  }
  else {
    print "405: solution dir $prefix/testmode/$arg/solutions not found\n"
  }
  closedir DIR;
}
else {
  print "203: unknown [$posted]\n";
}

sub markmode {
  return 1;
}

sub testmode {
  return (-e "$prefix/testing")?1:0;
}
