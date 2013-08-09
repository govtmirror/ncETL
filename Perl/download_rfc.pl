#!/usr/bin/perl

use strict;
use warnings;

use Cwd;
use File::stat;

my $dir = "/mnt/gdp-thredds-data-00/qpe/archive/motherlode";
my $file_type = "grib1";
my $time_offset = 3600;
my $index_prefix = "http://motherlode.ucar.edu/cgi-bin/ldm/genweb?native/grid/NPVU/RFC/";

# missing TSJU
my @rfcs = ("KALR", "KFWR", "KKRF", "KMSR", "KNES", "KORN",
	"KPTR", "KRHA", "KRSA", "KSTR", "KTAR", "KTIR", "KTUA");

chdir $dir;

foreach my $rfc (@rfcs) {
	mkdir "$dir/$rfc" unless(-e "$dir/$rfc");
	my $url = $index_prefix . $rfc;
	`wget -O tmp.html $url`;
	open(FILE, "<tmp.html");

	while (my $line = <FILE>) {
		if ($line =~ m/A HREF="([^"]*)".*modified\s+(.*)$/) {
			my $file_url = $1;
			my $mod_date = $2;
			if ($file_url =~ m/^.*\/([^\/]*\.$file_type)$/) {
				my $output_file = $1;
				my $old_file = "$rfc/$output_file";
				if (-e $old_file) {
					my $old_time = stat($old_file)->mtime;
					my $new_time = `date +%s -d "$mod_date"`;
					my $time_diff = $new_time - $old_time + $time_offset;
					print "$output_file -> $old_time $new_time\n";
					if ($time_diff > 60) { # close enough?
						`wget -O $output_file $file_url`;
						unlink $old_file;
						rename($output_file, $old_file);
						unlink "$old_file.gbx9";
						unlink "$old_file.ncx";
					}
				}
				else {
					`wget -O $old_file $file_url`;
				}
			}
		}
	}

	close FILE;
	unlink "tmp.html";
}
