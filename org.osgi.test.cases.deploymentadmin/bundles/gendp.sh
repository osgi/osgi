#!/bin/sh
echo Signing bundles
jarsigner -keystore ../megtck_keystore -storepass megtck -keypass megtck bundle001.jar megtck
jarsigner -keystore ../megtck_keystore -storepass megtck -keypass megtck bundle002.jar megtck
jarsigner -keystore ../megtck_keystore -storepass megtck -keypass megtck bundle003.jar megtck
jarsigner -keystore ../megtck_keystore -storepass megtck -keypass megtck bundle004.jar megtck
jarsigner -keystore ../megtck_keystore -storepass megtck -keypass megtck ../bundles_update/bundle001.jar megtck
jarsigner -keystore ../megtck_keystore -storepass megtck -keypass megtck ../bundles_update/bundle002.jar megtck
jarsigner -keystore ../megtck_keystore -storepass megtck -keypass megtck rp_bundle.jar megtck
jarsigner -keystore ../megtck_keystore -storepass megtck -keypass megtck rp_bundle2.jar megtck
jarsigner -keystore ../megtck_keystore -storepass megtck -keypass megtck rp_bundle3.jar megtck

echo Signing resources
jarsigner -keystore ../megtck_keystore -storepass megtck -keypass megtck conf.txt megtck
jarsigner -keystore ../megtck_keystore -storepass megtck -keypass megtck simple_resource.xml megtck
jarsigner -keystore ../megtck_keystore -storepass megtck -keypass megtck resource_processor_file.txt megtck
jarsigner -keystore ../megtck_keystore -storepass megtck -keypass megtck OSGI-INF/AUTOCONF.xml megtck

echo Creating deployment packages
jar -cvfm ../res/simple_dp.jar ../res/simple_dp.mf bundle001.jar bundle002.jar
jar -cvfm ../res/simple_clone_dp.jar ../res/simple_clone_dp.mf bundle001.jar bundle002.jar
jar -cvfm ../res/simple_higher_major_version_dp.jar ../res/simple_higher_major_version_dp.mf bundle001.jar bundle002.jar
jar -cvfm ../res/simple_higher_minor_version_dp.jar ../res/simple_higher_minor_version_dp.mf bundle001.jar bundle002.jar
jar -cvfm ../res/simple_higher_micro_version_dp.jar ../res/simple_higher_micro_version_dp.mf bundle001.jar bundle002.jar
jar -cvfm ../res/simple_resource_processor_dp.jar ../res/simple_resource_processor_dp.mf rp_bundle.jar conf.txt
jar -cvfm ../res/bundle_from_other_dp.jar ../res/bundle_from_other_dp.mf bundle001.jar bundle003.jar
jar -cvfm ../res/missing_name_header_dp.jar ../res/missing_name_header_dp.mf bundle001.jar
jar -cvfm ../res/bad_header_dp.jar ../res/bad_header_dp.mf bundle001.jar
jar -cvfm ../res/missing_manifest_dp.jar ../res/missing_manifest_dp.mf bundle001.jar
jar -cvfm ../res/symb_name_dif_from_manifest_dp.jar ../res/symb_name_dif_from_manifest_dp.mf bundle001.jar
jar -cvfm ../res/resource_processor_dp.jar ../res/resource_processor_dp.mf rp_bundle2.jar
jar -cvfm ../res/rp_resource_install_dp.jar ../res/rp_resource_install_dp.mf bundle003.jar resource_processor_file.txt
jar -cvfm ../res/rp_resource_update_dp.jar ../res/rp_resource_update_dp.mf resource_processor_file.txt
jar -cvfm ../res/rp_resource_uninstall_dp.jar ../res/rp_resource_uninstall_dp.mf
jar -cvfm ../res/missing_resource_fix_pack_dp.jar ../res/missing_resource_fix_pack_dp.mf
jar -cvfm ../res/missing_bundle_fix_pack_dp.jar ../res/missing_bundle_fix_pack_dp.mf simple_resource.xml
jar -cvfm ../res/add_bundle_fix_pack_dp.jar ../res/add_bundle_fix_pack_dp.mf bundle003.jar
jar -cvfm ../res/simple_resource_dp.jar ../res/simple_resource_dp.mf simple_resource.xml
jar -cvfm ../res/add_resource_fix_pack_dp.jar ../res/add_resource_fix_pack_dp.mf simple_resource.xml
jar -cvfm ../res/simple_unsigned_bundle_dp.jar ../res/simple_unsigned_bundle_dp.mf unsignedbundle.jar
jar -cvfm ../res/simple_nonsigned_dp.jar ../res/simple_nonsigned_dp.mf bundle001.jar
jar -cvfm ../res/simple_no_resource_dp.jar ../res/simple_no_resource_dp.mf bundle001.jar
jar -cvfm ../res/simple_no_bundle_dp.jar ../res/simple_no_bundle_dp.mf simple_resource.xml
jar -cvfm ../res/resource_from_other_dp.jar ../res/resource_from_other_dp.mf simple_resource.xml
jar -cvfm ../res/resource_processor_config_dp.jar ../res/resource_processor_config_dp.mf rp_bundle3.jar bundle004.jar OSGI-INF/AUTOCONF.xml
jar -cvfm ../res/different_name_and_version_dp.jar ../res/different_name_and_version_dp.mf bundle001.jar bundle002.jar

echo Moving update bundles to current dir
mv ../bundles_update/bundle001.jar ./
mv ../bundles_update/bundle002.jar ./
jar -cvfm ../res/simple_fix_pack_dp.jar ../res/simple_fix_pack_dp.mf bundle001.jar
jar -cvfm ../res/fix_pack_higher_range_dp.jar ../res/fix_pack_higher_range_dp.mf bundle001.jar
jar -cvfm ../res/fix_pack_lower_range_dp.jar ../res/fix_pack_lower_range_dp.mf bundle001.jar
jar -cvfm ../res/bundle_from_other_dp_dif_vers.jar ../res/bundle_from_other_dp_dif_vers.mf bundle001.jar

echo Signing deployment packages
jarsigner -keystore ../megtck_keystore -storepass megtck -keypass megtck ../res/simple_dp.jar megtck
jarsigner -keystore ../megtck_keystore -storepass megtck -keypass megtck ../res/simple_clone_dp.jar megtck
jarsigner -keystore ../megtck_keystore -storepass megtck -keypass megtck ../res/simple_higher_major_version_dp.jar megtck
jarsigner -keystore ../megtck_keystore -storepass megtck -keypass megtck ../res/simple_higher_minor_version_dp.jar megtck
jarsigner -keystore ../megtck_keystore -storepass megtck -keypass megtck ../res/simple_higher_micro_version_dp.jar megtck
jarsigner -keystore ../megtck_keystore -storepass megtck -keypass megtck ../res/simple_resource_processor_dp.jar megtck
jarsigner -keystore ../megtck_keystore -storepass megtck -keypass megtck ../res/bundle_from_other_dp.jar megtck
jarsigner -keystore ../megtck_keystore -storepass megtck -keypass megtck ../res/bundle_from_other_dp_dif_vers.jar megtck
jarsigner -keystore ../megtck_keystore -storepass megtck -keypass megtck ../res/missing_name_header_dp.jar megtck
jarsigner -keystore ../megtck_keystore -storepass megtck -keypass megtck ../res/bad_header_dp.jar megtck
jarsigner -keystore ../megtck_keystore -storepass megtck -keypass megtck ../res/missing_manifest_dp.jar megtck
jarsigner -keystore ../megtck_keystore -storepass megtck -keypass megtck ../res/symb_name_dif_from_manifest_dp.jar megtck
jarsigner -keystore ../megtck_keystore -storepass megtck -keypass megtck ../res/resource_processor_dp.jar megtck
jarsigner -keystore ../megtck_keystore -storepass megtck -keypass megtck ../res/rp_resource_install_dp.jar megtck
jarsigner -keystore ../megtck_keystore -storepass megtck -keypass megtck ../res/rp_resource_update_dp.jar megtck
jarsigner -keystore ../megtck_keystore -storepass megtck -keypass megtck ../res/rp_resource_uninstall_dp.jar megtck
jarsigner -keystore ../megtck_keystore -storepass megtck -keypass megtck ../res/simple_fix_pack_dp.jar megtck
jarsigner -keystore ../megtck_keystore -storepass megtck -keypass megtck ../res/missing_resource_fix_pack_dp.jar megtck
jarsigner -keystore ../megtck_keystore -storepass megtck -keypass megtck ../res/missing_bundle_fix_pack_dp.jar megtck
jarsigner -keystore ../megtck_keystore -storepass megtck -keypass megtck ../res/add_bundle_fix_pack_dp.jar megtck
jarsigner -keystore ../megtck_keystore -storepass megtck -keypass megtck ../res/fix_pack_higher_range_dp.jar megtck
jarsigner -keystore ../megtck_keystore -storepass megtck -keypass megtck ../res/simple_resource_dp.jar megtck
jarsigner -keystore ../megtck_keystore -storepass megtck -keypass megtck ../res/add_resource_fix_pack_dp.jar megtck
jarsigner -keystore ../megtck_keystore -storepass megtck -keypass megtck ../res/simple_no_resource_dp.jar megtck
jarsigner -keystore ../megtck_keystore -storepass megtck -keypass megtck ../res/simple_no_bundle_dp.jar megtck
jarsigner -keystore ../megtck_keystore -storepass megtck -keypass megtck ../res/resource_processor_config_dp.jar megtck
jarsigner -keystore ../megtck_keystore -storepass megtck -keypass megtck ../res/different_name_and_version_dp.jar megtck
jarsigner -keystore ../megtck_keystore -storepass megtck -keypass megtck ../res/simple_unsigned_bundle_dp.jar megtck
jarsigner -keystore ../megtck_keystore -storepass megtck -keypass megtck ../res/fix_pack_lower_range_dp.jar megtck

