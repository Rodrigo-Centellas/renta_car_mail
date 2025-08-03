// File: src/main/java/librerias/PasswordHasher.java
package librerias;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Hasher Bcrypt compatible con Laravel.
 * ➊ Genera el hash con jBCrypt (prefijo $2a$).
 * ➋ Sustituye el prefijo por $2y$ para que Laravel lo acepte.
 * ➌ El verificador hace el cambio inverso ($2y$ → $2a$) antes de llamar a jBCrypt.
 */
public class PasswordHasher {

    private static final int DEFAULT_COST = 12;   // igual que Laravel

    /* ─────────────── Hash ─────────────── */

    public static String hashPassword(String password) {
        return hashPassword(password, DEFAULT_COST);
    }

    public static String hashPassword(String password, int cost) {
        if (cost < 4 || cost > 31) {
            throw new IllegalArgumentException("El coste debe estar entre 4 y 31");
        }

        // 1) Hash normal ($2a$…)
        String hash = BCrypt.hashpw(password, BCrypt.gensalt(cost));

        // 2) Reemplazar prefijo $2a$ → $2y$
        if (hash.startsWith("$2a$")) {
            hash = "$2y$" + hash.substring(4);
        }

        return hash; // ahora comienza con $2y$
    }

    /* ───────────── Verificación ───────────── */

    public static boolean verifyPassword(String password, String laravelHash) {
        if (laravelHash == null || laravelHash.length() != 60) {
            return false;
        }

        // jBCrypt sólo entiende $2a$/$2b$. Convertimos si es $2y$
        String jbcryptHash = laravelHash.startsWith("$2y$")
                ? "$2a$" + laravelHash.substring(4)
                : laravelHash;

        return BCrypt.checkpw(password, jbcryptHash);
    }

    /* ───────────── Utilidades ───────────── */

    public static boolean isBcryptHash(String hash) {
        return hash != null
                && (hash.startsWith("$2y$") || hash.startsWith("$2b$")
                || hash.startsWith("$2a$"))
                && hash.length() == 60;
    }

    /* ───────────── Demo rápida ───────────── */

    public static void main(String[] args) {
        String pwd  = "123456";
        String hash = hashPassword(pwd);

        System.out.println("Hash Laravel‑ready: " + hash);
        System.out.println("Verifica? " + verifyPassword(pwd, hash));
    }
}
