@ECHO OFF

ECHO cleaning up before begin
del /q ..\org.osgi.test.cases.deploymentadmin.tc1\res\*.dp
del /q ..\org.osgi.test.cases.deploymentadmin.tc2\res\*.dp
del /q ..\org.osgi.test.cases.deploymentadmin.mo\delivered\*.dp
del /q bundles\*.jar
del /q bundles_update\*.jar

ECHO Building
call ..\licensed\ant\bin\ant
cd bundles

copy bundle001.jar wrong(path)\
copy bundle001.jar ..\..\org.osgi.test.cases.deploymentadmin.mo\delivered\
copy ..\bundles_update\bundle001.jar ..\..\org.osgi.test.cases.deploymentadmin.mo\res\
copy bundle001.jar bundle001_signed.jar
copy bundle002.jar bundle002_signed.jar
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest bundle001_signed.jar test
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest bundle002_signed.jar test

ECHO Creating common deployment packages
jar -cvfm ..\res\simple.dp ..\res\simple_dp.mf bundle001.jar bundle002.jar
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\simple.dp test
copy ..\res\simple.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\
move ..\res\simple.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

jar -cvfm ..\res\simple_no_resource.dp ..\res\simple_no_resource_dp.mf bundle001.jar
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\simple_no_resource.dp test
copy ..\res\simple_no_resource.dp ..\..\org.osgi.test.cases.deploymentadmin.mo\delivered\
copy ..\res\simple_no_resource.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\
move ..\res\simple_no_resource.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

jar -cvfm ..\res\simple_no_bundle.dp ..\res\simple_no_bundle_dp.mf simple_resource.xml
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\simple_no_bundle.dp test
copy ..\res\simple_no_bundle.dp ..\..\org.osgi.test.cases.deploymentadmin.mo\delivered\
copy ..\res\simple_no_bundle.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\
move ..\res\simple_no_bundle.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

jar -cvfm ..\res\simple_uninstall_bundle.dp ..\res\simple_uninstall_bundle_dp.mf
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\simple_uninstall_bundle.dp test
copy ..\res\simple_uninstall_bundle.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\
move ..\res\simple_uninstall_bundle.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

jar -cvfm ..\res\transactional_session.dp ..\res\transactional_session.mf bundle001.jar bundle002.jar simple_resource.xml
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\transactional_session.dp test
move ..\res\transactional_session.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

jar -cvfm ..\res\bundle_fail_res.dp ..\res\bundle_fail_res_dp.mf bundle005.jar bundle001.jar bundle002.jar simple_resource.xml conf.txt
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\bundle_fail_res.dp test
move ..\res\bundle_fail_res.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

jar -cvfm ..\res\bundle_fail_on_stop_res.dp ..\res\bundle_fail_on_stop_res_dp.mf bundle006.jar bundle001.jar bundle002.jar simple_resource.xml conf.txt
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\bundle_fail_on_stop_res.dp test
move ..\res\bundle_fail_on_stop_res.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

REM Renaming resource processors bundles to be compliant with deployment packages manifests
del rp_bundle.jar
del rp_bundle2.jar
del rp_bundle3.jar
del rp_bundle4.jar
ren rp_bundle_tc1_rp1.jar rp_bundle.jar
ren rp_bundle_tc1_rp2.jar rp_bundle2.jar
ren rp_bundle_tc1_rp3.jar rp_bundle3.jar
ren rp_bundle_tc1_rp4.jar rp_bundle4.jar

ECHO Creating TC1 deployment packages
jar -cvfm ..\res\simple_clone.dp ..\res\simple_clone_dp.mf bundle001.jar bundle002.jar
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\simple_clone.dp test
move ..\res\simple_clone.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\simple_higher_major_version.dp ..\res\simple_higher_major_version_dp.mf bundle001.jar bundle002.jar
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\simple_higher_major_version.dp test
move ..\res\simple_higher_major_version.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\simple_higher_minor_version.dp ..\res\simple_higher_minor_version_dp.mf bundle001.jar bundle002.jar
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\simple_higher_minor_version.dp test
move ..\res\simple_higher_minor_version.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\simple_higher_micro_version.dp ..\res\simple_higher_micro_version_dp.mf bundle001.jar bundle002.jar
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\simple_higher_micro_version.dp test
move ..\res\simple_higher_micro_version.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\simple_resource_processor.dp ..\res\simple_resource_processor_dp.mf rp_bundle.jar conf.txt
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\simple_resource_processor.dp test
move ..\res\simple_resource_processor.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\bundle_from_other.dp ..\res\bundle_from_other_dp.mf bundle001.jar bundle003.jar
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\bundle_from_other.dp test
copy ..\res\bundle_from_other.dp ..\..\org.osgi.test.cases.deploymentadmin.mo\delivered\
move ..\res\bundle_from_other.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\missing_name_header.dp ..\res\missing_name_header_dp.mf bundle001.jar
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\missing_name_header.dp test
copy ..\res\missing_name_header.dp ..\..\org.osgi.test.cases.deploymentadmin.mo\delivered\
move ..\res\missing_name_header.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\missing_b_version_header.dp ..\res\missing_b_version_header_dp.mf bundle001.jar
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\missing_b_version_header.dp test
move ..\res\missing_b_version_header.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\missing_bsn_header.dp ..\res\missing_bsn_header_dp.mf bundle001.jar
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\missing_bsn_header.dp test
move ..\res\missing_bsn_header.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\missing_fix_pack_header.dp ..\res\missing_fix_pack_header_dp.mf bundle001.jar
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\missing_fix_pack_header.dp test
move ..\res\missing_fix_pack_header.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\missing_res_name_header.dp ..\res\missing_res_name_header_dp.mf bundle001.jar
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\missing_fix_pack_header.dp test
move ..\res\missing_res_name_header.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\missing_version_header.dp ..\res\missing_version_header_dp.mf bundle001.jar
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\missing_fix_pack_header.dp test
move ..\res\missing_version_header.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\bad_header.dp ..\res\bad_header_dp.mf bundle001.jar
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\bad_header.dp test
move ..\res\bad_header.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfM ..\res\missing_manifest.dp bundle001.jar
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\missing_manifest.dp test
move ..\res\missing_manifest.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\symb_name_dif_from_manifest.dp ..\res\symb_name_dif_from_manifest_dp.mf bundle001.jar
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\symb_name_dif_from_manifest.dp test
copy ..\res\symb_name_dif_from_manifest.dp ..\..\org.osgi.test.cases.deploymentadmin.mo\delivered\
move ..\res\symb_name_dif_from_manifest.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\version_dif_from_manifest.dp ..\res\version_dif_from_manifest_dp.mf bundle001.jar
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\version_dif_from_manifest.dp test
move ..\res\version_dif_from_manifest.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\resource_processor.dp ..\res\resource_processor_dp.mf rp_bundle.jar
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\resource_processor.dp test
move ..\res\resource_processor.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\missing_resource_fix_pack.dp ..\res\missing_resource_fix_pack_dp.mf
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\missing_resource_fix_pack.dp test
copy ..\res\missing_resource_fix_pack.dp ..\..\org.osgi.test.cases.deploymentadmin.mo\delivered\
move ..\res\missing_resource_fix_pack.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\missing_bundle_fix_pack.dp ..\res\missing_bundle_fix_pack_dp.mf simple_resource.xml
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\missing_bundle_fix_pack.dp test
copy ..\res\missing_bundle_fix_pack.dp ..\..\org.osgi.test.cases.deploymentadmin.mo\delivered\
move ..\res\missing_bundle_fix_pack.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\add_bundle_fix_pack.dp ..\res\add_bundle_fix_pack_dp.mf bundle003.jar
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\add_bundle_fix_pack.dp test
move ..\res\add_bundle_fix_pack.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\simple_resource.dp ..\res\simple_resource_dp.mf simple_resource.xml conf.txt
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\simple_resource.dp test
copy ..\res\simple_resource.dp ..\..\org.osgi.test.cases.deploymentadmin.mo\delivered\
move ..\res\simple_resource.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\add_resource_fix_pack.dp ..\res\add_resource_fix_pack_dp.mf simple_resource.xml
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\add_resource_fix_pack.dp test
move ..\res\add_resource_fix_pack.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\simple_unsigned_bundle.dp ..\res\simple_unsigned_bundle_dp.mf unsignedbundle.jar
move ..\res\simple_unsigned_bundle.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\simple_unsigned.dp ..\res\simple_unsigned_dp.mf bundle001.jar
move ..\res\simple_unsigned.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\resource_from_other.dp ..\res\resource_from_other_dp.mf simple_resource.xml
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\resource_from_other.dp test
copy ..\res\resource_from_other.dp ..\..\org.osgi.test.cases.deploymentadmin.mo\delivered\
move ..\res\resource_from_other.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\different_name_and_version.dp ..\res\different_name_and_version_dp.mf bundle001.jar bundle002.jar
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\different_name_and_version.dp test
move ..\res\different_name_and_version.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\system.dp ..\res\system.mf bundle001.jar bundle002.jar
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\system.dp test
move ..\res\system.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\wrong_order.dp ..\res\wrong_order.mf simple_resource.xml rp_bundle.jar
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\wrong_order.dp test
move ..\res\wrong_order.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\untrusted.dp ..\res\untrusted.mf bundle001.jar bundle002.jar
jarsigner -keystore ..\invalid_keystore -storepass invalid -keypass invalid ..\res\untrusted.dp invalid
copy ..\res\untrusted.dp ..\..\org.osgi.test.cases.deploymentadmin.mo\delivered\
move ..\res\untrusted.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\wrong_path.dp ..\res\wrong_path.mf wrong(path)\bundle001.jar
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\wrong_path.dp test
move ..\res\wrong_path.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\bundle_doesnt_throw_exception.dp ..\res\bundle_throws_exception_dp.mf bundle005.jar
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\bundle_doesnt_throw_exception.dp test
move ..\res\bundle_doesnt_throw_exception.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\bundle_throws_exception_stop.dp ..\res\bundle_throws_exception_stop_dp.mf bundle006.jar
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\bundle_throws_exception_stop.dp test
move ..\res\bundle_throws_exception_stop.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\block_session.dp ..\res\block_session_tc1.mf rp_bundle3.jar conf.txt
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\block_session.dp test
move ..\res\block_session.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\wrong_bsn.dp ..\res\wrong_bsn.mf bundle001.jar
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\wrong_bsn.dp test
move ..\res\wrong_bsn.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\wrong_bversion.dp ..\res\wrong_bversion.mf bundle001.jar
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\wrong_bversion.dp test
move ..\res\wrong_bversion.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\wrong_customizer.dp ..\res\wrong_customizer.mf rp_bundle.jar
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\wrong_customizer.dp test
move ..\res\wrong_customizer.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\wrong_dp_missing.dp ..\res\wrong_dp_missing.mf bundle001.jar
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\wrong_dp_missing.dp test
move ..\res\wrong_dp_missing.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\wrong_fix_pack.dp ..\res\wrong_fix_pack.mf bundle001.jar
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\wrong_fix_pack.dp test
move ..\res\wrong_fix_pack.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\wrong_name.dp ..\res\wrong_name.mf bundle001.jar
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\wrong_name.dp test
move ..\res\wrong_name.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\wrong_rp.dp ..\res\wrong_rp.mf bundle001.jar
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\wrong_rp.dp test
move ..\res\wrong_rp.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\wrong_version.dp ..\res\wrong_version.mf bundle001.jar
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\wrong_version.dp test
move ..\res\wrong_version.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\simple_res_proc_uninstall.dp ..\res\simple_resource_processor_uninstall.mf rp_bundle4.jar
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\simple_res_proc_uninstall.dp test
move ..\res\simple_res_proc_uninstall.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\simple_resource_uninstall.dp ..\res\simple_resource_uninstall_dp.mf conf.txt
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\simple_resource_uninstall.dp test
move ..\res\simple_resource_uninstall.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\strange_path.dp ..\res\strange_name_dp.mf st_ra.ge-dir/bundle001.jar
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\strange_path.dp test
move ..\res\strange_path.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\localized.dp ..\res\localized_dp.mf bundle003.jar OSGI-INF/l10n/dp.properties
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\localized.dp test
move ..\res\localized.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\session_test.dp ..\res\session_test.mf rp_bundle.jar rp_bundle2.jar simple_resource.xml conf.txt
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\session_test.dp test
move ..\res\session_test.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\resource_processor2.dp ..\res\resource_processor2_dp.mf rp_bundle2.jar
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\resource_processor2.dp test
move ..\res\resource_processor2.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\non_customizer_rp.dp ..\res\non_customizer_rp.mf rp_bundle.jar simple_resource.xml
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\non_customizer_rp.dp test
move /y ..\res\non_customizer_rp.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

REM This jar is not generated because these signature files only are not valid for the newest created bundles. We copy the old one.
REM jar -cvfM ..\res\signing_files_not_next.dp META-INF\MANIFEST.MF META-INF\README.TXT META-INF\TEST.SF META-INF\TEST.RSA bundle001.jar bundle002.jar
REM copy ..\res\signing_files_not_next.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\
copy ..\res\signing_files_not_next.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

ECHO Moving update bundles to current dir
move /y ..\bundles_update\bundle001.jar .\
move /y ..\bundles_update\bundle002.jar .\
move /y ..\bundles_update\bundle005.jar .\

jar -cvfm ..\res\simple_fix_pack.dp ..\res\simple_fix_pack_dp.mf bundle001.jar
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\simple_fix_pack.dp test
copy ..\res\simple_fix_pack.dp ..\..\org.osgi.test.cases.deploymentadmin.mo\delivered\
REM It copies the simple_fix_pack.dp to other directory, so we can use the same archive name when updating a deployment package
copy /y ..\res\simple_fix_pack.dp ..\..\org.osgi.test.cases.deploymentadmin.mo\res\
del ..\..\org.osgi.test.cases.deploymentadmin.mo\res\simple.dp
ren ..\..\org.osgi.test.cases.deploymentadmin.mo\res\simple_fix_pack.dp simple.dp
move ..\res\simple_fix_pack.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\fix_pack_higher_range.dp ..\res\fix_pack_higher_range_dp.mf bundle001.jar
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\fix_pack_higher_range.dp test
move ..\res\fix_pack_higher_range.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\fix_pack_lower_range.dp ..\res\fix_pack_lower_range_dp.mf bundle001.jar
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\fix_pack_lower_range.dp test
copy ..\res\fix_pack_lower_range.dp ..\..\org.osgi.test.cases.deploymentadmin.mo\delivered\
move ..\res\fix_pack_lower_range.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\bundle_from_other_dp_dif_vers.dp ..\res\bundle_from_other_dp_dif_vers.mf bundle001.jar
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\bundle_from_other_dp_dif_vers.dp test
move ..\res\bundle_from_other_dp_dif_vers.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\bundle_throws_exception.dp ..\res\bundle_throws_exception_update.mf bundle005.jar
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\bundle_throws_exception.dp test
copy ..\res\bundle_throws_exception.dp ..\..\org.osgi.test.cases.deploymentadmin.mo\delivered\
move ..\res\bundle_throws_exception.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

REM Renaming resource processors bundles to be compliant with deployment packages manifests
del rp_bundle.jar
del rp_bundle2.jar
del rp_bundle3.jar
del rp_bundle4.jar
ren rp_bundle_tc2_rp1.jar rp_bundle.jar
ren rp_bundle_tc2_rp2.jar rp_bundle2.jar
ren rp_bundle_tc2_rp3.jar rp_bundle3.jar
ren rp_bundle_tc2_rp4.jar rp_bundle4.jar

ECHO Creating TC2 deployment packages
jar -cvfm ..\res\session_resource_processor.dp ..\res\session_resource_processor_dp.mf bundle001.jar bundle002.jar rp_bundle3.jar simple_resource.xml
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\session_resource_processor.dp test
move ..\res\session_resource_processor.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

jar -cvfm ..\res\rp_resource_install.dp ..\res\rp_resource_install_dp.mf bundle003.jar resource_processor_file.txt
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\rp_resource_install.dp test
move ..\res\rp_resource_install.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

jar -cvfm ..\res\rp_resource_update.dp ..\res\rp_resource_update_dp.mf resource_processor_file.txt
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\rp_resource_update.dp test
move ..\res\rp_resource_update.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

jar -cvfm ..\res\rp_resource_uninstall.dp ..\res\rp_resource_uninstall_dp.mf
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\rp_resource_uninstall.dp test
move ..\res\rp_resource_uninstall.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

jar -cvfm ..\res\session_update_test.dp ..\res\session_update_test.mf rp_bundle.jar rp_bundle2.jar simple_resource.xml conf.txt
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\session_update_test.dp test
move ..\res\session_update_test.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

jar -cvfm ..\res\auto_config.dp ..\res\auto_config_dp.mf bundle004.jar bundle007.jar OSGI-INF\AUTOCONF.xml
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\auto_config.dp test
move ..\res\auto_config.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

jar -cvfm ..\res\block_session.dp ..\res\block_session_tc2.mf rp_bundle4.jar conf.txt
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\block_session.dp test
move ..\res\block_session.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

jar -cvfM ..\res\manifest_not_1st_file.dp META-INF\README.TXT META-INF\MANIFEST.MF META-INF\TEST.SF META-INF\TEST.RSA bundle001.jar bundle002.jar
copy ..\res\manifest_not_1st_file.dp ..\..\org.osgi.test.cases.deploymentadmin.mo\delivered\
move ..\res\manifest_not_1st_file.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

jar -cvfm ..\res\rp_from_other_dp.dp ..\res\rp_from_other_dp.mf conf.txt
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\rp_from_other_dp.dp test
move ..\res\rp_from_other_dp.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

jar -cvfm ..\res\installation_fails.dp ..\res\installation_fails_dp.mf simple_resource.xml conf.txt
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\installation_fails.dp test
move ..\res\installation_fails.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

jar -cvfm ..\res\resource_processor_rp3.dp ..\res\resource_processor_rp3.mf rp_bundle3.jar
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\resource_processor_rp3.dp test
move ..\res\resource_processor_rp3.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

jar -cvfm ..\res\bsn_dif_from_manifest.dp ..\res\bsn_dif_from_manifest_dp.mf bundle001.jar simple_resource.xml
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\bsn_dif_from_manifest.dp test
move ..\res\bsn_dif_from_manifest.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

jar -cvfm ..\res\bversion_dif_from_manifest.dp ..\res\bversion_dif_from_manifest_dp.mf bundle001.jar simple_resource.xml
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\bversion_dif_from_manifest.dp test
move ..\res\bversion_dif_from_manifest.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

jar -cvfm ..\res\simple_bundle_res.dp ..\res\simple_bundle_res_dp.mf bundle001.jar bundle002.jar bundle003.jar simple_resource.xml conf.txt
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\simple_bundle_res.dp test
move ..\res\simple_bundle_res.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

jar -cvfM ..\res\signing_files_not_next.dp META-INF\MANIFEST.MF META-INF\README.TXT META-INF\TEST.SF META-INF\TEST.RSA bundle001.jar bundle002.jar
copy ..\res\signing_files_not_next.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\
move ..\res\signing_files_not_next.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

jar -cvfm ..\res\session_test.dp ..\res\session_test.mf rp_bundle.jar rp_bundle2.jar simple_resource.xml conf.txt
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\session_test.dp test
move ..\res\session_test.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

jar -cvfm ..\res\resource_processor2.dp ..\res\resource_processor2_dp.mf rp_bundle2.jar
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\resource_processor2.dp test
move ..\res\resource_processor2.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

jar -cvfm ..\res\simple_resource_rp3.dp ..\res\simple_resource_rp3.mf simple_resource.xml
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\simple_resource_rp3.dp test
move ..\res\simple_resource_rp3.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

jar -cvfm ..\res\resource_processor_customizer.dp ..\res\resource_processor_customizer.mf rp_bundle.jar simple_resource.xml
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\resource_processor_customizer.dp test
move ..\res\resource_processor_customizer.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

REM Renaming resource processors bundles to be compliant with deployment packages manifests
del rp_bundle.jar
del rp_bundle2.jar
del rp_bundle3.jar
del rp_bundle4.jar

ren rp_bundle_dmo_rp1.jar rp_bundle.jar
ren rp_bundle_dmo_rp2.jar rp_bundle2.jar
ren rp_bundle_dmo_rp3.jar rp_bundle3.jar
ren rp_bundle_dmo_rp4.jar rp_bundle4.jar

ECHO Creating Deployment Management Object packages (only the new ones)
REM Deployment Management Object uses a sgined bundle001 to verify the MetaNode of the Deployed subtree
ren bundle001.jar bundle001_temp.jar
ren bundle001_signed.jar bundle001.jar
ren bundle002.jar bundle002_temp.jar
ren bundle002_signed.jar bundle002.jar
jar -cvfm ..\res\simple.dp ..\res\simple_dp.mf bundle001.jar bundle002.jar
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\simple.dp test
move ..\res\simple.dp ..\..\org.osgi.test.cases.deploymentadmin.mo\delivered\
del bundle001.jar
ren bundle001_temp.jar bundle001.jar
del bundle002.jar
ren bundle002_temp.jar bundle002.jar

jar -cvfm ..\res\rp_not_able_to_commit.dp ..\res\rp_not_able_to_commit.mf rp_bundle2.jar simple_resource.xml
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\rp_not_able_to_commit.dp test
move ..\res\rp_not_able_to_commit.dp ..\..\org.osgi.test.cases.deploymentadmin.mo\delivered\

jar -cvfm ..\res\resource_processor_dmo.dp ..\res\resource_processor_dp.mf rp_bundle.jar
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\resource_processor_dmo.dp test
move ..\res\resource_processor_dmo.dp ..\..\org.osgi.test.cases.deploymentadmin.mo\delivered\
ren ..\..\org.osgi.test.cases.deploymentadmin.mo\delivered\resource_processor_dmo.dp resource_processor.dp

jar -cvfm ..\res\block_session.dp ..\res\block_session_dmo.mf rp_bundle3.jar conf.txt
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\block_session.dp test
move ..\res\block_session.dp ..\..\org.osgi.test.cases.deploymentadmin.mo\delivered\

jar -cvfm ..\res\rp_throws_no_such_resource.dp ..\res\rp_throws_no_such_resource.mf rp_bundle4.jar
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\rp_throws_no_such_resource.dp test
move ..\res\rp_throws_no_such_resource.dp ..\..\org.osgi.test.cases.deploymentadmin.mo\delivered\

jar -cvfm ..\res\dp_installs_resource_for_rp4.dp ..\res\dp_installs_resource_for_rp4.mf simple_resource.xml
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\dp_installs_resource_for_rp4.dp test
move ..\res\dp_installs_resource_for_rp4.dp ..\..\org.osgi.test.cases.deploymentadmin.mo\delivered\

jar -cvfm ..\res\resource_processor_customizer.dp ..\res\resource_processor_customizer.mf rp_bundle.jar simple_resource.xml
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\resource_processor_customizer.dp test
move ..\res\resource_processor_customizer.dp ..\..\org.osgi.test.cases.deploymentadmin.mo\delivered\

jar -cvfm ..\res\non_customizer_rp.dp ..\res\non_customizer_rp.mf rp_bundle.jar simple_resource.xml
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\non_customizer_rp.dp test
move ..\res\non_customizer_rp.dp ..\..\org.osgi.test.cases.deploymentadmin.mo\delivered\

jar -cvfm ..\res\dp_throws_resource_violation.dp ..\res\dp_throws_resource_violation.mf rp_bundle4.jar simple_resource.xml
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\dp_throws_resource_violation.dp test
move ..\res\dp_throws_resource_violation.dp ..\..\org.osgi.test.cases.deploymentadmin.mo\delivered\

jar -cvfm ..\res\rp_from_other_dp_dmo.dp ..\res\rp_from_other_dp_dmo.mf conf.txt
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\rp_from_other_dp_dmo.dp test
move ..\res\rp_from_other_dp_dmo.dp ..\..\org.osgi.test.cases.deploymentadmin.mo\delivered\
ren ..\..\org.osgi.test.cases.deploymentadmin.mo\delivered\rp_from_other_dp_dmo.dp rp_from_other_dp.dp

jar -cvfm ..\res\dp_removes_resource_for_rp4.dp ..\res\dp_removes_resource_for_rp4.mf
jarsigner -keystore ..\..\cnf\certificate\.keystore -storepass testtest -keypass testtest ..\res\dp_removes_resource_for_rp4.dp test
move ..\res\dp_removes_resource_for_rp4.dp ..\..\org.osgi.test.cases.deploymentadmin.mo\delivered\

REM Backing to the previous directory
cd ..