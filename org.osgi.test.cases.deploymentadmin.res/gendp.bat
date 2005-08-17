@ECHO OFF
ECHO cleaning up before begin
del /q ..\org.osgi.test.cases.deploymentadmin.tc1\res\*.dp
del /q ..\org.osgi.test.cases.deploymentadmin.tc2\res\*.dp
cd bundles

ECHO Signing bundles
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck bundle001.jar megtck
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck bundle002.jar megtck
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck bundle003.jar megtck
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck bundle004.jar megtck
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck bundle005.jar megtck
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck bundle006.jar megtck
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\bundles_update\bundle001.jar megtck
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\bundles_update\bundle002.jar megtck
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\bundles_update\bundle005.jar megtck
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck rp_bundle_tc1_rp1.jar megtck
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck rp_bundle_tc1_rp2.jar megtck
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck rp_bundle_tc2_rp1.jar megtck
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck rp_bundle_tc2_rp2.jar megtck
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck rp_bundle_tc2_rp3.jar megtck

ECHO Signing resources
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck conf.txt megtck
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck simple_resource.xml megtck
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck resource_processor_file.txt megtck
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck OSGI-INF\AUTOCONF.xml megtck

copy bundle001.jar wrong(path)\

ECHO Creating common deployment packages
jar -cvfm ..\res\simple.dp ..\res\simple_dp.mf bundle001.jar bundle002.jar
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\simple.dp megtck
copy ..\res\simple.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\
move ..\res\simple.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

REM Renaming resource processors bundles to be compliant with deployment packages manifests
del rp_bundle.jar
del rp_bundle2.jar
ren rp_bundle_tc1_rp1.jar rp_bundle.jar
ren rp_bundle_tc1_rp2.jar rp_bundle2.jar

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

jar -cvfm ..\res\bad_header.dp ..\res\bad_header_dp.mf bundle001.jar
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\bad_header.dp megtck
move ..\res\bad_header.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\missing_manifest.dp ..\res\missing_manifest_dp.mf bundle001.jar
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\missing_manifest.dp megtck
move ..\res\missing_manifest.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\symb_name_dif_from_manifest.dp ..\res\symb_name_dif_from_manifest_dp.mf bundle001.jar
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\symb_name_dif_from_manifest.dp megtck
move ..\res\symb_name_dif_from_manifest.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

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

jar -cvfm ..\res\simple_no_resource.dp ..\res\simple_no_resource_dp.mf bundle001.jar
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\simple_no_resource.dp megtck
move ..\res\simple_no_resource.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\simple_no_bundle.dp ..\res\simple_no_bundle_dp.mf simple_resource.xml
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\simple_no_bundle.dp megtck
move ..\res\simple_no_bundle.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

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

jar -cvfm ..\res\wrong_version.dp ..\res\wrong_version.mf bundle001.jar bundle002.jar
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\wrong_version.dp megtck
move ..\res\wrong_version.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\simple_uninstall_bundle.dp ..\res\simple_uninstall_bundle_dp.mf
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\simple_uninstall_bundle.dp megtck
move ..\res\simple_uninstall_bundle.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\bundle_doesnt_throw_exception.dp ..\res\bundle_throws_exception_dp.mf bundle005.jar
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\bundle_doesnt_throw_exception.dp megtck
move ..\res\bundle_doesnt_throw_exception.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

jar -cvfm ..\res\bundle_throws_exception_stop.dp ..\res\bundle_throws_exception_stop_dp.mf bundle006.jar
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\bundle_throws_exception_stop.dp megtck
move ..\res\bundle_throws_exception_stop.dp ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

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
jar -cvfm ..\res\wrong_format.jar ..\res\wrong_format.mf bundle001.jar bundles\bundle002.jar
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\wrong_format.jar megtck
move /y ..\res\wrong_format.jar ..\..\org.osgi.test.cases.deploymentadmin.tc1\res\

REM Renaming resource processors bundles to be compliant with deployment packages manifests
del rp_bundle.jar
del rp_bundle2.jar
del rp_bundle3.jar
ren rp_bundle_tc2_rp1.jar rp_bundle.jar
ren rp_bundle_tc2_rp2.jar rp_bundle2.jar
ren rp_bundle_tc2_rp3.jar rp_bundle3.jar

ECHO Creating TC2 deployment packages
jar -cvfm ..\res\session_resource_processor.dp ..\res\session_resource_processor_dp.mf bundle001.jar bundle002.jar rp_bundle3.jar simple_resource.xml
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\session_resource_processor.dp megtck
move ..\res\session_resource_processor.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

jar -cvfm ..\res\resource_processor2.dp ..\res\resource_processor2_dp.mf rp_bundle2.jar
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\resource_processor2.dp megtck
move ..\res\resource_processor2.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

jar -cvfm ..\res\rp_resource_install.dp ..\res\rp_resource_install_dp.mf bundle003.jar resource_processor_file.txt
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\rp_resource_install.dp megtck
move ..\res\rp_resource_install.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

jar -cvfm ..\res\rp_resource_update.dp ..\res\rp_resource_update_dp.mf resource_processor_file.txt
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\rp_resource_update.dp megtck
move ..\res\rp_resource_update.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

jar -cvfm ..\res\rp_resource_uninstall.dp ..\res\rp_resource_uninstall_dp.mf
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\rp_resource_uninstall.dp megtck
move ..\res\rp_resource_uninstall.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

jar -cvfm ..\res\session_test.dp ..\res\session_test.mf rp_bundle.jar rp_bundle2.jar simple_resource.xml conf.txt
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\session_test.dp megtck
move ..\res\session_test.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

jar -cvfm ..\res\session_update_test.dp ..\res\session_update_test.mf rp_bundle.jar rp_bundle2.jar simple_resource.xml conf.txt
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\session_update_test.dp megtck
move ..\res\session_update_test.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

jar -cvfm ..\res\transactional_session.dp ..\res\transactional_session.mf bundle001.jar bundle002.jar simple_resource.xml
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\transactional_session.dp megtck
move ..\res\transactional_session.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

jar -cvfm ..\res\auto_config.dp ..\res\auto_config_dp.mf bundle004.jar OSGI-INF\AUTOCONF.xml
jarsigner -keystore ..\megtck_keystore -storepass megtck -keypass megtck ..\res\auto_config.dp megtck
move ..\res\auto_config.dp ..\..\org.osgi.test.cases.deploymentadmin.tc2\res\

REM Backing to the previous directory
cd ..