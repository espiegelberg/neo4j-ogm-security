##Neo4j Data Encryption with OGM

###Introduction
Security is a fact of modern life and, as a direct result, a fact of modern software. While all non-trivial systems require at least some basic level of functional protection, how much security is appropriate is typically driven by the system's actors and use cases as well as the value of the information to be protected.

While Neo4j does not currently deal with data encryption [explicitly](http://neo4j.com/docs/stable/capabilities-data-security.html), for scenarios where additional security is desired two approaches are common: encrypting the filesystem the database sits upon and encrypting the data itself from the application.

Encrypting the database filesystem is a straightforward, beneficial step that results in enhancing the protection of the data on disk. Yet filesystem encryption alone is insufficient to fully protect the data. This is due to Neo4j employing a REST-based architecture that responds to Cypher statements sent as web service calls with the response to these calls – i.e., the data – traveling over the network in cleartext. 

While the logical next step is to make use of HTTPS to encrypt network communication, some applications benefit from or even require additional protection, such as restricting access to the data to only those that are authorized to work with it. This is where application layer encryption comes in.

Application layer encryption is the process of having the application dynamically alter the data at runtime by performing encryption and decryption before and after data is written to or read from the database. Many widely recognized industry security standards, such as HIPPA and FERPA for the health and education fields, can be addressed through the use of application layer security.

###Using OGM's @Converter for Application-Level Encryption

In the case of Java-based applications, Neo4j's Object Graph Mapping (OGM) library can be used to implement application-level security. Analogous to the Java Persistence API (JPA) or Hibernate for traditional relational databases and the infrastructure underlying Spring Data Neo4j, OGM allows software developers to annotate the system’s domain model and have data then seamlessly marshaled and unmarshaled between Neo4j and the domain at runtime. 

Because Neo4j may persist your data in a format different from that of your domain model, such as storing a Date either as Long or as a String, OGM offers the concept of an AttributeConverter. Used to convert a given property in and out of a desired format, OGM offers several concrete implementations to handle Java Enums, Numbers, and Dates out of the box. Creating a custom AttributeConverter implementation offers an excellent entry-point for developing application level encryption.

Below is an example, available from [GitHub](https://github.com/espiegelberg/neo4j-ogm-security), of a simple custom AttributeConverter that encrypts String properties using the popular [Jasypt](http://www.jasypt.org) encryption library. 
```
public class SecureStringPropertyConverter implements AttributeConverter<String, String> {
...
	 // Encrypt the value before it is persisted to the database
	public String toGraphProperty(String value) {
		String encryptedText = textEncryptor.encrypt(value);
		return encryptedText;	
	}

	// Decrypt the database value to the value set on the object model
	public String toEntityAttribute(String value) {
		String plaintext = textEncryptor.decrypt(result);
		return plaintext;
	}
...
}
```
With the required dependencies and the above class added to your project, it is now trivial to selectively add property-level encryption to your application. For example:
```
…
import com.mile24.example.neo4j.ogm.typeconversion.SecureStringPropertyConverter;
...
public class Department extends Entity {
…
@Convert(SecureStringPropertyConverter.class)
private String name;

@Convert(SecureStringPropertyConverter.class)
private String description;

	private String publicDescription;
...
}
```
With the custom @Convert annotation applied as in the above sample, OGM will invoke the SecureStringPropertyConverter for the name and description properties, transparently encrypting or decrypting as needed each time a Department is persisted or retrieved. While this example centers on Strings, the same approach applies equally well to all additional property types, including dates, numbers, and binary. 

Because the security is applied on a per-property basis – rather than at the object level – the flexibility is retained to judiciously enhance the security of only those properties that warrant it while properties of low security value or that need to be indexed or searched outside the application, such as the publicDescription property, can remain unencrypted.

###The Good With The Bad: Pros and Cons

As with all software design, there are pros and cons.

Working to its favor, this straightforward and easy-to-implement approach results in fine-grained, property-level encryption that keeps your data secure both on disk as well as during transmission across the network to and from the Neo4j server, protecting it from unauthorized access outside the application. 

The particular encryption process employed can be completely tailored to match your particular needs and real-world, hardcore encryption could be easily implemented by making use of any Java Cryptography Architecture (JCA) provider, such as Jasypt, to provide military strength encryption.

Rarely in life do you get anything of value for free or without disadvantages. Additional security never improves system performance and this case is no exception: the encryption process itself incurs computational overhead in terms of memory and processing power and the fact that data is transmitted over the wire in its encrypted format – which is typically much larger than its plaintext counterpart – will negatively impact network utilization. 

By the very nature of security, the encrypted data becomes more difficult to work with outside the application and database features, such as indexes, searching and casual Cypher querying each become unviable. Finally, existing data will need to be transformed into the desired encrypted format to coincide with the release of the enhanced application or care should be taken to ensure the application can successfully work with both encrypted and unencrypted values.

###Conclusion

Security in software is not a question of if, but of how much, and employing the right amount is a critical part of your overall application architecture and implementation. By making use of OGM's @Converter infrastructure, any Java-based application can easily, seamlessly and transparently secure data on disk and in-flight to and from Neo4j. The provided code samples, while simplistic, demonstrate an approach that can be used to quickly and easily provide industrial strength security to real-world applications.

###Biography
Eric Spiegelberg is the founder of [Mile 24](http://www.miletwentyfour.com), a Twin Cities based software consultancy. As an architect with over 15 years of experience with the Java platform, Eric holds a BS in Computer Science and a MS in Software Engineering, is an avid technologist, a published technical author, and life long learner. Outside of technology Eric is a high performance and instrument rated private pilot, has run the Paris Marathon in France, and enjoys an interest in travel.
