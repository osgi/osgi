package org.osgi.service.enocean;

public interface EnOceanVersionInfo {

		/**
		 * Raw version info, encoded on 4 bytes.
		 * @return  [main,beta,alpha,build]
		 */
		public byte[] raw();
		
		/**
		 * @return Main version info
		 */
		public int main();
		
		/**
		 * @return Beta version info
		 */
		public int beta();
		
		/**
		 * @return Alpha version info
		 */
		public int alpha();
		
		/**
		 * @return Build version info
		 */
		public int build();

}
