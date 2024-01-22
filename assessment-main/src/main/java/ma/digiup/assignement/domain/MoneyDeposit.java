package ma.digiup.assignement.domain;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "MONEY_DEPOSIT")
public class MoneyDeposit {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(precision = 16, scale = 2, nullable = false)
  private BigDecimal  montant;

  @Column
  @Temporal(TemporalType.TIMESTAMP)
  private Date dateExecution;

  @Column
  private String nom_prenom_emetteur;

  @ManyToOne
  private Compte compteBeneficiaire;

  @Column(length = 200)
  private String motifDeposit;

  @PrePersist
  private void validateDepositAmount() {
    if (montant.compareTo(BigDecimal.valueOf(10000L)) > 0) {
      throw new IllegalArgumentException("Le montant du dépôt dépasse la limite maximale");
    }
  }
  public BigDecimal getMontant() {
    return montant;
  }

  public void setMontant(BigDecimal montant) {
    this.montant = montant;
  }

  public Date getDateExecution() {
    return dateExecution;
  }

  public void setDateExecution(Date dateExecution) {
    this.dateExecution = dateExecution;
  }

  public Compte getCompteBeneficiaire() {
    return compteBeneficiaire;
  }

  public void setCompteBeneficiaire(Compte compteBeneficiaire) {
    this.compteBeneficiaire = compteBeneficiaire;
  }

  public String getMotifDeposit() {
    return motifDeposit;
  }

  public void setMotifDeposit(String motifDeposit) {
    this.motifDeposit = motifDeposit;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getNom_prenom_emetteur() {
    return nom_prenom_emetteur;
  }

  public void setNom_prenom_emetteur(String nom_prenom_emetteur) {
    this.nom_prenom_emetteur = nom_prenom_emetteur;
  }
}
