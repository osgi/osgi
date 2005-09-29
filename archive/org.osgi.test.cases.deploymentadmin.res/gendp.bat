@ECHO OFF

ECHO cleaning up before begin
del /q ..\org.osgi.test.cases.deploymentadmin.tc1\res\*.dp
del /q ..\org.osgi.test.cases.deploymentadmin.tc2\res\*.dp
del /q bundles\*.jar
del /q bundles_update\*.jar

ECHO Building
call ..\cnf\ant\bin\ant
cd bundles

copy bundle001.jar wrong(path)\

ECHO Creating common deployment packages
jar -cvfm ..\res\simple.dp ..\res\simple_dp.mf bundle001.jar bundle002.jar
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\simple.dp megtck
copy ..\res\simple.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\
move ..\res\simple.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

jar -cvfm ..\res\simple_no_resource.dp ..\res\simple_no_resource_dp.mf bundle001.jar
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\simple_no_resource.dp megtck
move ..\res\simple_no_resource.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\
move ..\res\simple_no_resource.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

jar -cvfm ..\res\simple_no_bundle.dp ..\res\simple_no_bundle_dp.mf simple_resource.xml
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\simple_no_bundle.dp megtck
move ..\res\simple_no_bundle.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\
move ..\res\simple_no_bundle.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

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
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\simple_clone.dp megtck
move ..\res\simple_clone.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\simple_higher_major_version.dp ..\res\simple_higher_major_version_dp.mf bundle001.jar bundle002.jar
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\simple_higher_major_version.dp megtck
move ..\res\simple_higher_major_version.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\simple_higher_minor_version.dp ..\res\simple_higher_minor_version_dp.mf bundle001.jar bundle002.jar
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\simple_higher_minor_version.dp megtck
move ..\res\simple_higher_minor_version.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\simple_higher_micro_version.dp ..\res\simple_higher_micro_version_dp.mf bundle001.jar bundle002.jar
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\simple_higher_micro_version.dp megtck
move ..\res\simple_higher_micro_version.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\simple_resource_processor.dp ..\res\simple_resource_processor_dp.mf rp_bundle.jar conf.txt
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\simple_resource_processor.dp megtck
move ..\res\simple_resource_processor.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\bundle_from_other.dp ..\res\bundle_from_other_dp.mf bundle001.jar bundle003.jar
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\bundle_from_other.dp megtck
move ..\res\bundle_from_other.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\missing_name_header.dp ..\res\missing_name_header_dp.mf bundle001.jar
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\missing_name_header.dp megtck
move ..\res\missing_name_header.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\missing_b_version_header.dp ..\res\missing_b_version_header_dp.mf bundle001.jar
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\missing_b_version_header.dp megtck
move ..\res\missing_b_version_header.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\missing_bsn_header.dp ..\res\missing_bsn_header_dp.mf bundle001.jar
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\missing_bsn_header.dp megtck
move ..\res\missing_bsn_header.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\missing_fix_pack_header.dp ..\res\missing_fix_pack_header_dp.mf bundle001.jar
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\missing_fix_pack_header.dp megtck
move ..\res\missing_fix_pack_header.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\missing_res_name_header.dp ..\res\missing_res_name_header_dp.mf bundle001.jar
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\missing_fix_pack_header.dp megtck
move ..\res\missing_res_name_header.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\missing_version_header.dp ..\res\missing_version_header_dp.mf bundle001.jar
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\missing_fix_pack_header.dp megtck
move ..\res\missing_version_header.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\bad_header.dp ..\res\bad_header_dp.mf bundle001.jar
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\bad_header.dp megtck
move ..\res\bad_header.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfM ..\res\missing_manifest.dp bundle001.jar
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\missing_manifest.dp megtck
move ..\res\missing_manifest.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\symb_name_dif_from_manifest.dp ..\res\symb_name_dif_from_manifest_dp.mf bundle001.jar
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\symb_name_dif_from_manifest.dp megtck
move ..\res\symb_name_dif_from_manifest.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\version_dif_from_manifest.dp ..\res\version_dif_from_manifest_dp.mf bundle001.jar
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\version_dif_from_manifest.dp megtck
move ..\res\version_dif_from_manifest.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\resource_processor.dp ..\res\resource_processor_dp.mf rp_bundle.jar
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\resource_processor.dp megtck
move ..\res\resource_processor.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\missing_resource_fix_pack.dp ..\res\missing_resource_fix_pack_dp.mf
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\missing_resource_fix_pack.dp megtck
move ..\res\missing_resource_fix_pack.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\missing_bundle_fix_pack.dp ..\res\missing_bundle_fix_pack_dp.mf simple_resource.xml
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\missing_bundle_fix_pack.dp megtck
move ..\res\missing_bundle_fix_pack.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\add_bundle_fix_pack.dp ..\res\add_bundle_fix_pack_dp.mf bundle003.jar
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\add_bundle_fix_pack.dp megtck
move ..\res\add_bundle_fix_pack.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\simple_resource.dp ..\res\simple_resource_dp.mf simple_resource.xml conf.txt
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\simple_resource.dp megtck
move ..\res\simple_resource.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\add_resource_fix_pack.dp ..\res\add_resource_fix_pack_dp.mf simple_resource.xml
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\add_resource_fix_pack.dp megtck
move ..\res\add_resource_fix_pack.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\simple_unsigned_bundle.dp ..\res\simple_unsigned_bundle_dp.mf unsignedbundle.jar
move ..\res\simple_unsigned_bundle.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\simple_unsigned.dp ..\res\simple_unsigned_bundle_dp.mf bundle001.jar
move ..\res\simple_unsigned.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\resource_from_other.dp ..\res\resource_from_other_dp.mf simple_resource.xml
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\resource_from_other.dp megtck
move ..\res\resource_from_other.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\different_name_and_version.dp ..\res\different_name_and_version_dp.mf bundle001.jar bundle002.jar
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\different_name_and_version.dp megtck
move ..\res\different_name_and_version.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\system.dp ..\res\system.mf bundle001.jar bundle002.jar
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\system.dp megtck
move ..\res\system.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\wrong_order.dp ..\res\wrong_order.mf simple_resource.xml rp_bundle.jar
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\wrong_order.dp megtck
move ..\res\wrong_order.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\untrusted.dp ..\res\untrusted.mf bundle001.jar bundle002.jar
jarsigner -keystore ..\invalid_keystore -storepass invalid -keypass invalid ..\res\untrusted.dp invalid
move ..\res\untrusted.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\wrong_path.dp ..\res\wrong_path.mf wrong(path)\bundle001.jar
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\wrong_path.dp megtck
move ..\res\wrong_path.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\simple_uninstall_bundle.dp ..\res\simple_uninstall_bundle_dp.mf
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\simple_uninstall_bundle.dp megtck
move ..\res\simple_uninstall_bundle.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\bundle_doesnt_throw_exception.dp ..\res\bundle_throws_exception_dp.mf bundle005.jar
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\bundle_doesnt_throw_exception.dp megtck
move ..\res\bundle_doesnt_throw_exception.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\bundle_throws_exception_stop.dp ..\res\bundle_throws_exception_stop_dp.mf bundle006.jar
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\bundle_throws_exception_stop.dp megtck
move ..\res\bundle_throws_exception_stop.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\block_session.dp ..\res\block_session_tc1.mf rp_bundle3.jar conf.txt
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\block_session.dp megtck
move ..\res\block_session.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\wrong_bsn.dp ..\res\wrong_bsn.mf bundle001.jar
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\wrong_bsn.dp megtck
move ..\res\wrong_bsn.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\wrong_bversion.dp ..\res\wrong_bversion.mf bundle001.jar
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\wrong_bversion.dp megtck
move ..\res\wrong_bversion.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\wrong_customizer.dp ..\res\wrong_customizer.mf rp_bundle.jar
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\wrong_customizer.dp megtck
move ..\res\wrong_customizer.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\wrong_dp_missing.dp ..\res\wrong_dp_missing.mf bundle001.jar
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\wrong_dp_missing.dp megtck
move ..\res\wrong_dp_missing.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\wrong_fix_pack.dp ..\res\wrong_fix_pack.mf bundle001.jar
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\wrong_fix_pack.dp megtck
move ..\res\wrong_fix_pack.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\wrong_name.dp ..\res\wrong_name.mf bundle001.jar
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\wrong_name.dp megtck
move ..\res\wrong_name.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\wrong_rp.dp ..\res\wrong_rp.mf bundle001.jar
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\wrong_rp.dp megtck
move ..\res\wrong_rp.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\wrong_version.dp ..\res\wrong_version.mf bundle001.jar
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\wrong_version.dp megtck
move ..\res\wrong_version.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\simple_res_proc_uninstall.dp ..\res\simple_resource_processor_uninstall.mf rp_bundle4.jar
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\simple_res_proc_uninstall.dp megtck
move ..\res\simple_res_proc_uninstall.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\simple_resource_uninstall.dp ..\res\simple_resource_uninstall_dp.mf conf.txt
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\simple_resource_uninstall.dp megtck
move ..\res\simple_resource_uninstall.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\strange_path.dp ..\res\strange_path_dp.mf bundle001.jar
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\strange_path.dp megtck
move ..\res\strange_path.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\localized.dp ..\res\localized_dp.mf bundle003.jar OSGI-INF/l10n/dp.properties
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\localized.dp megtck
move ..\res\localized.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\session_test.dp ..\res\session_test.mf rp_bundle.jar rp_bundle2.jar simple_resource.xml conf.txt
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\session_test.dp megtck
move ..\res\session_test.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\resource_processor2.dp ..\res\resource_processor2_dp.mf rp_bundle2.jar
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\resource_processor2.dp megtck
move ..\res\resource_processor2.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

ECHO Moving update bundles to current dir
move /y ..\bundles_update\bundle001.jar .\
move /y ..\bundles_update\bundle002.jar .\
move /y ..\bundles_update\bundle005.jar .\

jar -cvfm ..\res\simple_fix_pack.dp ..\res\simple_fix_pack_dp.mf bundle001.jar
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\simple_fix_pack.dp megtck
move ..\res\simple_fix_pack.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\fix_pack_higher_range.dp ..\res\fix_pack_higher_range_dp.mf bundle001.jar
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\fix_pack_higher_range.dp megtck
move ..\res\fix_pack_higher_range.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\fix_pack_lower_range.dp ..\res\fix_pack_lower_range_dp.mf bundle001.jar
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\fix_pack_lower_range.dp megtck
move ..\res\fix_pack_lower_range.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\bundle_from_other_dp_dif_vers.dp ..\res\bundle_from_other_dp_dif_vers.mf bundle001.jar
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\bundle_from_other_dp_dif_vers.dp megtck
move ..\res\bundle_from_other_dp_dif_vers.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\bundle_throws_exception.dp ..\res\bundle_throws_exception_update.mf bundle005.jar
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\bundle_throws_exception.dp megtck
move ..\res\bundle_throws_exception.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

ECHO Generating wrong format deployment package
jar -cvfm ..\res\wrong_format.wrg ..\res\wrong_format.mf bundle001.jar bundles\bundle002.jar
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\wrong_format.wrg megtck
move /y ..\res\wrong_format.wrg ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

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
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\session_resource_processor.dp megtck
move ..\res\session_resource_processor.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

jar -cvfm ..\res\rp_resource_install.dp ..\res\rp_resource_install_dp.mf bundle003.jar resource_processor_file.txt
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\rp_resource_install.dp megtck
move ..\res\rp_resource_install.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

jar -cvfm ..\res\rp_resource_update.dp ..\res\rp_resource_update_dp.mf resource_processor_file.txt
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\rp_resource_update.dp megtck
move ..\res\rp_resource_update.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

jar -cvfm ..\res\rp_resource_uninstall.dp ..\res\rp_resource_uninstall_dp.mf
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\rp_resource_uninstall.dp megtck
move ..\res\rp_resource_uninstall.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

jar -cvfm ..\res\session_update_test.dp ..\res\session_update_test.mf rp_bundle.jar rp_bundle2.jar simple_resource.xml conf.txt
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\session_update_test.dp megtck
move ..\res\session_update_test.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

jar -cvfm ..\res\transactional_session.dp ..\res\transactional_session.mf bundle001.jar bundle002.jar simple_resource.xml
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\transactional_session.dp megtck
move ..\res\transactional_session.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

jar -cvfm ..\res\auto_config.dp ..\res\auto_config_dp.mf bundle004.jar OSGI-INF\AUTOCONF.xml
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\auto_config.dp megtck
move ..\res\auto_config.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

jar -cvfm ..\res\block_session.dp ..\res\block_session_tc2.mf rp_bundle4.jar conf.txt
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\block_session.dp megtck
move ..\res\block_session.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

jar -cvfM ..\res\manifest_not_1st_file.dp bundle001.jar bundle002.jar META-INF\README.txt META-INF\MANIFEST.mf
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\manifest_not_1st_file.dp megtck
move ..\res\manifest_not_1st_file.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

jar -cvfm ..\res\rp_from_other_dp.dp ..\res\rp_from_other_dp.mf rp_bundle3.jar conf.txt
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\rp_from_other_dp.dp megtck
move ..\res\rp_from_other_dp.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

jar -cvfm ..\res\installation_fails.dp ..\res\installation_fails_dp.mf bundle005.jar simple_resource.xml
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\installation_fails.dp megtck
move ..\res\installation_fails.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

jar -cvfm ..\res\resource_processor_rp3.dp ..\res\resource_processor_rp3.mf rp_bundle3.jar
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\resource_processor_rp3.dp megtck
move ..\res\resource_processor_rp3.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

jar -cvfm ..\res\bsn_dif_from_manifest.dp ..\res\bsn_dif_from_manifest_dp.mf bundle001.jar simple_resource.xml
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\bsn_dif_from_manifest.dp megtck
move ..\res\bsn_dif_from_manifest.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

jar -cvfm ..\res\bversion_dif_from_manifest.dp ..\res\bversion_dif_from_manifest_dp.mf bundle001.jar simple_resource.xml
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\bversion_dif_from_manifest.dp megtck
move ..\res\bversion_dif_from_manifest.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

jar -cvfm ..\res\simple_bundle_res.dp ..\res\simple_bundle_res_dp.mf bundle001.jar bundle002.jar bundle003.jar simple_resource.xml conf.txt
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\simple_bundle_res.dp megtck
move ..\res\simple_bundle_res.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

jar -cvfm ..\res\bundle_fail_res.dp ..\res\bundle_fail_res_dp.mf bundle005.jar bundle001.jar bundle002.jar simple_resource.xml conf.txt
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\bundle_fail_res.dp megtck
move ..\res\bundle_fail_res.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

jar -cvfm ..\res\bundle_fail_on_stop_res.dp ..\res\bundle_fail_on_stop_res_dp.mf bundle006.jar bundle001.jar bundle002.jar simple_resource.xml conf.txt
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\bundle_fail_on_stop_res.dp megtck
move ..\res\bundle_fail_on_stop_res.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

jar -cvf ..\res\signing_files_not_next.dp bundle001.jar bundle002.jar META-INF\MANIFEST.mf META-INF\README.txt META-INF\MEGTCK.DSA META-INF\MEGTCK.SF
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\signing_files_not_next.dp megtck
move ..\res\signing_files_not_next.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

jar -cvfm ..\res\session_test.dp ..\res\session_test.mf rp_bundle.jar rp_bundle2.jar simple_resource.xml conf.txt
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\session_test.dp megtck
move ..\res\session_test.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

jar -cvfm ..\res\resource_processor2.dp ..\res\resource_processor2_dp.mf rp_bundle2.jar
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\resource_processor2.dp megtck
move ..\res\resource_processor2.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

REM Backing to the previous directory
cd ..