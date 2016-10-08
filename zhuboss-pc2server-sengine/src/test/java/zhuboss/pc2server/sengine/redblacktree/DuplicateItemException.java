package zhuboss.pc2server.sengine.redblacktree;

/**
* Exception class for duplicate item errors
* in search tree insertions.
* @author Mark Allen Weiss
*/
public class DuplicateItemException extends RuntimeException
{
 /**
	 * 
	 */
	private static final long serialVersionUID = 2297173057766380907L;
/**
  * Construct this exception object.
  */
 public DuplicateItemException( )
 {
     super( );
 }
 /**
  * Construct this exception object.
  * @param message the error message.
  */
 public DuplicateItemException( String message )
 {
     super( message );
 }
}