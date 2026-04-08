package fr.utc.miage.transpitrack.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="commentary")
public class Commentary {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "commentary_seq")
    private Long id;

    private String message;

    @ManyToOne
    private User author;

    @OneToOne
    private Activity activity;

    public Commentary(String message, User autor, Activity activity) {
        this.message = message;
        this.author = autor;
        this.activity = activity;
    }

    

    
}
