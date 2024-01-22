package ma.digiup.assignement.web;

import ma.digiup.assignement.domain.MoneyDeposit;
import ma.digiup.assignement.domain.Utilisateur;
import ma.digiup.assignement.repository.UtilisateurRepository;
import ma.digiup.assignement.domain.Compte;
import ma.digiup.assignement.domain.Transfer;
import ma.digiup.assignement.dto.TransferDto;
import ma.digiup.assignement.exceptions.CompteNonExistantException;
import ma.digiup.assignement.exceptions.SoldeDisponibleInsuffisantException;
import ma.digiup.assignement.exceptions.TransactionException;
import ma.digiup.assignement.repository.CompteRepository;
import ma.digiup.assignement.repository.TransferRepository;
import ma.digiup.assignement.repository.moneyDepositRepository;
import ma.digiup.assignement.service.AuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/transfers")
class TransferController {

    public static final int MONTANT_MAXIMAL = 10000;

    Logger LOGGER = LoggerFactory.getLogger(TransferController.class);

    @Autowired
    private CompteRepository rep1;
    @Autowired
    private TransferRepository re2;
    @Autowired
    private AuditService monservice;
    @Autowired
    private moneyDepositRepository moneyDepositRepository;

    private final UtilisateurRepository re3;

    @Autowired
    TransferController(UtilisateurRepository re3) {
        this.re3 = re3;
    }

    @GetMapping("listDesTransferts")
    List<Transfer> loadAll() {
        LOGGER.info("Lister des utilisateurs");
        var all = re2.findAll();

        if (CollectionUtils.isEmpty(all)) {
            return null;
        } else {
            return CollectionUtils.isEmpty(all) ? all : null;
        }
    }

    @GetMapping("listOfAccounts")
    List<Compte> loadAllCompte() {
        List<Compte> all = rep1.findAll();

        if (CollectionUtils.isEmpty(all)) {
            return null;
        } else {
            return all;
        }
    }

    @GetMapping("lister_utilisateurs")
    List<Utilisateur> loadAllUtilisateur() {
        List<Utilisateur> all = re3.findAll();

        if (CollectionUtils.isEmpty(all)) {
            return null;
        } else {
            return all;
        }
    }

    @PostMapping("/executerTransfers")
    @ResponseStatus(HttpStatus.CREATED)
    public void createTransaction(@RequestBody TransferDto transferDto)
            throws SoldeDisponibleInsuffisantException, CompteNonExistantException, TransactionException {
        Compte c1 = rep1.findByNrCompte(transferDto.getNrCompteEmetteur());
        Compte f12 = rep1
                .findByNrCompte(transferDto.getNrCompteBeneficiaire());

        if (c1 == null) {
            System.out.println("Compte Non existant");
            throw new CompteNonExistantException("Compte Non existant");
        }

        if (f12 == null) {
            System.out.println("Compte Non existant");
            throw new CompteNonExistantException("Compte Non existant");
        }

        if (transferDto.getMontant().equals(null)) {
            System.out.println("Montant vide");
            throw new TransactionException("Montant vide");
        } else if (transferDto.getMontant().intValue() == 0) {
            System.out.println("Montant vide");
            throw new TransactionException("Montant vide");
        } else if (transferDto.getMontant().intValue() < 10) {
            System.out.println("Montant minimal de transfer non atteint");
            throw new TransactionException("Montant minimal de transfer non atteint");
        } else if (transferDto.getMontant().intValue() > MONTANT_MAXIMAL) {
            System.out.println("Montant maximal de transfer dépassé");
            throw new TransactionException("Montant maximal de transfer dépassé");
        }

        if (transferDto.getMotif().length() < 0) {
            System.out.println("Motif vide");
            throw new TransactionException("Motif vide");
        }

        if (c1.getSolde().intValue() - transferDto.getMontant().intValue() < 0) {
            LOGGER.error("Solde insuffisant pour l'utilisateur");
        }

        if (c1.getSolde().intValue() - transferDto.getMontant().intValue() < 0) {
            LOGGER.error("Solde insuffisant pour l'utilisateur");
        }

        c1.setSolde(c1.getSolde().subtract(transferDto.getMontant()));
        rep1.save(c1);

        f12
                .setSolde(new BigDecimal(f12.getSolde().intValue() + transferDto.getMontant().intValue()));
        rep1.save(f12);

        Transfer transfer = new Transfer();
        transfer.setDateExecution(transferDto.getDate());
        transfer.setCompteBeneficiaire(f12);
        transfer.setCompteEmetteur(c1);
        transfer.setMontantTransfer(transferDto.getMontant());

        re2.save(transfer);

        monservice.auditTransfer("Transfer depuis " + transferDto.getNrCompteEmetteur() + " vers " + transferDto
                        .getNrCompteBeneficiaire() + " d'un montant de " + transferDto.getMontant()
                        .toString());
    }
    @PostMapping("/effectuerDepot")
    @ResponseStatus(HttpStatus.CREATED)
    public void makeDeposit(@RequestBody TransferDto depositDto)
            throws CompteNonExistantException, TransactionException {
        Compte compteBeneficiaire = rep1.findByNrCompte(depositDto.getNrCompteBeneficiaire());

        if (compteBeneficiaire == null) {
            throw new CompteNonExistantException("Compte bénéficiaire introuvable");
        }

        if (depositDto.getMontant().compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransactionException("Montant de dépôt invalide");
        } else if (depositDto.getMontant().compareTo(BigDecimal.valueOf(10000L)) > 0) {
            throw new TransactionException("Le montant du dépôt dépasse la limite maximale");
        }

        compteBeneficiaire.setSolde(compteBeneficiaire.getSolde().add(depositDto.getMontant()));
        rep1.save(compteBeneficiaire);

        MoneyDeposit deposit = new MoneyDeposit();
        deposit.setMontant(depositDto.getMontant());
        deposit.setDateExecution(new Date());
        deposit.setCompteBeneficiaire(compteBeneficiaire);
        deposit.setMotifDeposit(depositDto.getMotif());

        moneyDepositRepository.save(deposit);

        monservice.auditDeposit("Dépôt sur " + depositDto.getNrCompteBeneficiaire() +
                " d'un montant de " + depositDto.getMontant().toString());
    }

    private void save(Transfer Transfer) {
        re2.save(Transfer);
    }
}
