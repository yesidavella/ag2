package com.ag2.presentacion.diseño;

import com.ag2.controlador.LinkAdminAbstractController;
import com.ag2.controlador.NodeAdminAbstractController;
import com.ag2.presentacion.IGU;
import com.ag2.presentacion.ActionTypeEmun;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

public abstract class GraphNode implements Selectable, Serializable {

    private static ImageView IMG_VW_DENY_LINK = new ImageView(
            new Image(GraphNode.class.getResourceAsStream("../../../../recursos/imagenes/prohibido_enlace.png")));
    public static boolean inicioGeneracionDeEnlace = false;
    private static GraphNode nodoAComodin = null;
    protected transient Image imagen = null;
    private transient Line enlaceComodin;
    private transient ImageView imageView;
    protected transient Label lblNombre;
    private transient DropShadow dropShadow;
    protected transient VBox cuadroExteriorResaltado;
    private transient Group group;
    private String name = null;
    private ArrayList<NodeListener> nodosListener = new ArrayList<NodeListener>();
    private boolean estaEliminado = false;
    private boolean selecionado = false;
    private String urlDeImagen;
    private boolean arrastrando = false;
    private NodeAdminAbstractController controladorAbstractoAdminNodo;
    private LinkAdminAbstractController controladorAdminEnlace;
    private short alto;
    private short ancho;
    private short cantidadDeEnlaces = 0;
    private String nombreOriginal;
    protected short pasoDeSaltoLinea;
    private short altoInicial = 0;
    private HashMap<String, String> propertiesNode;
    private HashMap<String, String> subPropertiesNode;
    private GraphDesignGroup grupoDeDiseno;
    private double layoutX;
    private double layoutY;

    public GraphNode(GraphDesignGroup grupoDeDiseno, String nombre, String urlDeImagen, NodeAdminAbstractController controladorAbstractoAdminNodo, LinkAdminAbstractController ctrlAbsAdminEnlace) {

        this.grupoDeDiseno = grupoDeDiseno;
        this.controladorAbstractoAdminNodo = controladorAbstractoAdminNodo;
        this.controladorAdminEnlace = ctrlAbsAdminEnlace;
        this.urlDeImagen = urlDeImagen;
        this.name = nombre;
        this.nombreOriginal = nombre;
        propertiesNode = new HashMap<String, String>();
        subPropertiesNode = new HashMap<String, String>();

        initTransientObjects();


    }

    public void initTransientObjects() {

        group = new Group();
        dropShadow = new DropShadow();
        group.setEffect(dropShadow);
        lblNombre = new Label(name);
        lblNombre.setTextFill(Color.BLACK);
        lblNombre.setTextAlignment(TextAlignment.CENTER);
        lblNombre.setStyle("-fx-font: bold 8pt 'Arial'; -fx-background-color:#CCD4EC");


        enlaceComodin = new Line();
        cuadroExteriorResaltado = new VBox();
        imagen = new Image(getClass().getResourceAsStream(urlDeImagen));
        imageView = new ImageView(imagen);
        cuadroExteriorResaltado.setAlignment(Pos.CENTER);
        cuadroExteriorResaltado.getChildren().addAll(imageView, lblNombre);

        group.getChildren().addAll(cuadroExteriorResaltado);
        group.setScaleX(0.5);
        group.setScaleY(-0.5);

        establecerEventoOnMouseClicked();
        establecerEventoOnMousePressed();
        establecerEventoOnMouseDragged();
        establecerEventoOnMouseReleased();
        establecerEventoOnMouseEntered();
        establecerEventoOnMouseExit();
    }

    public HashMap<String, String> getNodeProperties() {
        return propertiesNode;
    }

    public void setAltoInicial(short altoInicial) {
        this.altoInicial = altoInicial;
    }

    public short getAltoInicial() {
        return altoInicial;
    }

    public String getNombreOriginal() {
        return nombreOriginal;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GraphNode) {
            GraphNode nodoGrafico = (GraphNode) obj;
            return nombreOriginal.equals(nodoGrafico.getNombreOriginal());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (this.nombreOriginal != null ? this.nombreOriginal.hashCode() : 0);
        return hash;
    }

    public boolean isSelecionado() {
        return selecionado;
    }

    public void seleccionar(boolean selecionado) {
        this.selecionado = selecionado;
        if (!selecionado) {
            dropShadow.setColor(Color.WHITESMOKE);
            dropShadow.setSpread(.2);
            dropShadow.setWidth(20);
            dropShadow.setHeight(20);

            cuadroExteriorResaltado.getStyleClass().remove("nodoSeleccionado");
            cuadroExteriorResaltado.getStyleClass().add("nodoNoSeleccionado");
        } else {
            controladorAbstractoAdminNodo.queryProperties(this);
            cuadroExteriorResaltado.getStyleClass().remove("nodoNoSeleccionado");
            cuadroExteriorResaltado.getStyleClass().add("nodoSeleccionado");

            group.toFront();
            dropShadow.setColor(Color.web("#44FF00"));
            dropShadow.setSpread(.2);
            dropShadow.setWidth(25);
            dropShadow.setHeight(25);
        }
    }

    @Override
    public String toString() {
        return name;
    }

    private void establecerEventoOnMouseEntered() {
        group.setOnMouseEntered(new EventHandler<MouseEvent>() {

            public void handle(MouseEvent mouseEvent) {

                ActionTypeEmun tipoDeBotonSeleccionado = IGU.getEstadoTipoBoton();
                GraphNode nodoGrafico = GraphNode.this;

                if (tipoDeBotonSeleccionado == ActionTypeEmun.ENLACE) {
                    group.setCursor(tipoDeBotonSeleccionado.getImagenSobreObjetoCursor());
                } else {
                    group.setCursor(tipoDeBotonSeleccionado.getImagenCursor());
                }

                if (inicioGeneracionDeEnlace == false) {
                    nodoAComodin = null;
                }

                if (tipoDeBotonSeleccionado == ActionTypeEmun.ENLACE) {

                    if (nodoAComodin != null && nodoAComodin != nodoGrafico && GraphNode.inicioGeneracionDeEnlace) {

                        if (nodoGrafico.puedeGenerarEnlaceCon(nodoAComodin)) {



                            grupoDeDiseno.remove(enlaceComodin);

                            GraphLink enlaceGrafico = new GraphLink(grupoDeDiseno, nodoAComodin, nodoGrafico, controladorAdminEnlace);
                            enlaceGrafico.addArcosInicialAlGrupo();

                            nodoAComodin.setCantidadDeEnlaces((short) (nodoAComodin.getCantidadDeEnlaces() + 1));
                            nodoGrafico.setCantidadDeEnlaces((short) (nodoGrafico.getCantidadDeEnlaces() + 1));

                            nodoAComodin.getGroup().toFront();
                            nodoGrafico.getGroup().toFront();
                        } else {
                            nodoGrafico.playDenyLinkAnimation();
                        }
                    }

                } else if (tipoDeBotonSeleccionado == ActionTypeEmun.ELIMINAR) {
                    group.setCursor(tipoDeBotonSeleccionado.getImagenSobreObjetoCursor());
                }
                nodoAComodin = null;
            }
        });
    }

    private void establecerEventoOnMousePressed() {
        group.setOnMousePressed(new EventHandler<MouseEvent>() {

            public void handle(MouseEvent mouseEvent) {

                setAncho((short) cuadroExteriorResaltado.getWidth());
                setAlto((short) cuadroExteriorResaltado.getHeight());

                if (IGU.getEstadoTipoBoton() == ActionTypeEmun.ENLACE) {

                    GraphNode nodoGrafico = GraphNode.this;
                    nodoAComodin = nodoGrafico;

                    double x = nodoGrafico.getLayoutX();
                    double y = nodoGrafico.getLayoutY();
                    enlaceComodin.setStartX(x + ancho / 2);
                    enlaceComodin.setStartY(y + alto / 2);
                    enlaceComodin.setEndX(x + ancho / 2);
                    enlaceComodin.setEndY(y + alto / 2);
                    grupoDeDiseno.add(enlaceComodin);
                    enlaceComodin.toFront();
                    nodoGrafico.group.toFront();

                    if (mouseEvent.isPrimaryButtonDown() && group.isHover()) {
                        GraphNode.inicioGeneracionDeEnlace = true;
                    }
                }
                group.setScaleX(1);
                group.setScaleY(-1);
            }
        });
    }

    private void establecerEventoOnMouseDragged() {
        group.setOnMouseDragged(new EventHandler<MouseEvent>() {

            public void handle(MouseEvent mouseEvent) {


                if (IGU.getEstadoTipoBoton() == ActionTypeEmun.PUNTERO) {
                    setLayoutX(getLayoutX() + mouseEvent.getX() - ancho / 2);
                    setLayoutY(getLayoutY() - (mouseEvent.getY() - alto / 2));
                    updateNodoListener();
                } else if (IGU.getEstadoTipoBoton() == ActionTypeEmun.ENLACE) {
                    arrastrando = true;
                    enlaceComodin.setEndX(getLayoutX() + (mouseEvent.getX()));
                    enlaceComodin.setEndY(getLayoutY() + alto - (mouseEvent.getY()));
                }
            }
        });
    }

    private void establecerEventoOnMouseReleased() {
        group.setOnMouseReleased(new EventHandler<MouseEvent>() {

            public void handle(MouseEvent mouseEvent) {

                group.setScaleX(0.5);
                group.setScaleY(-0.5);

                if (IGU.getEstadoTipoBoton() == ActionTypeEmun.ENLACE) {

                    grupoDeDiseno.remove(enlaceComodin);

                }
            }
        });
    }

    private void establecerEventoOnMouseClicked() {
        group.setOnMouseClicked(new EventHandler<MouseEvent>() {

            public void handle(MouseEvent mouseEvent) {

                GraphNode nodoGrafico = GraphNode.this;

                if (IGU.getEstadoTipoBoton() == ActionTypeEmun.ELIMINAR) {

                    nodoGrafico.setEliminado(true);
                    grupoDeDiseno.remove(nodoGrafico);
                    grupoDeDiseno.eliminarNodeListaNavegacion(nodoGrafico);
                    controladorAbstractoAdminNodo.removeNode(nodoGrafico);

                }

                if (IGU.getEstadoTipoBoton() == ActionTypeEmun.PUNTERO) {

                    Selectable objSeleccionado = grupoDeDiseno.getObjetoGraficoSelecionado();

                    if (!arrastrando) {
                        if (objSeleccionado == nodoGrafico) {
                            objSeleccionado.seleccionar(false);
                            grupoDeDiseno.setObjetoGraficoSelecionado(null);
                        } else {
                            if (objSeleccionado == null) {
                                nodoGrafico.seleccionar(true);
                                grupoDeDiseno.setObjetoGraficoSelecionado(nodoGrafico);
                            } else {
                                objSeleccionado.seleccionar(false);
                                nodoGrafico.seleccionar(true);
                                grupoDeDiseno.setObjetoGraficoSelecionado(nodoGrafico);
                            }
                        }

                    } else {
                        if (nodoGrafico != objSeleccionado) {
                            if (objSeleccionado == null) {
                                nodoGrafico.seleccionar(true);
                                grupoDeDiseno.setObjetoGraficoSelecionado(nodoGrafico);
                            } else {
                                objSeleccionado.seleccionar(false);
                                nodoGrafico.seleccionar(true);
                                grupoDeDiseno.setObjetoGraficoSelecionado(nodoGrafico);
                            }
                        }
                    }
                    arrastrando = false;
                }
                updateNodoListener();
            }
        });
    }

    private void establecerEventoOnMouseExit() {

        group.setOnMouseExited(new EventHandler<MouseEvent>() {

            public void handle(MouseEvent mouseEvent) {
                //System.out.println("Exit:" + getNombre());

                if (!mouseEvent.isPrimaryButtonDown()) {
                    GraphNode.inicioGeneracionDeEnlace = false;
                    nodoAComodin = null;
                }

            }
        });
    }

    public void addNodoListener(NodeListener listenerNodo) {
        nodosListener.add(listenerNodo);
    }

    public void removeNodoListener(NodeListener listenerNodo) {
        nodosListener.remove(listenerNodo);
    }

    public void updateNodoListener() {
        layoutX = this.getLayoutX();
        layoutY = this.getLayoutY();

        for (NodeListener nodoListener : nodosListener) {
            nodoListener.update();
        }
    }

    public Image getImagen() {
        return imagen;
    }

    public boolean isEliminado() {
        return estaEliminado;
    }

    public void setEliminado(boolean eliminado) {
        this.estaEliminado = eliminado;
    }

    public String getNombre() {
        return name;
    }

    public void setNombre(String nombre) {
        this.name = nombre;
        lblNombre.setText(formatearNombreConSaltoDeLineas(nombre));
    }

    private void readObject(ObjectInputStream inputStream) {
        try {
            inputStream.defaultReadObject();
            System.out.println("read :" + name);
            if (grupoDeDiseno.isSerializableComplete()) 
            {
                initTransientObjects();
                getGroup().setLayoutX(getLayoutX());
                getGroup().setLayoutY(getLayoutY());
                seleccionar(false);
                grupoDeDiseno.getGroup().getChildren().add(getGroup());
                System.out.println("read load post :" + name);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeObject(ObjectOutputStream objectOutputStream) {
        try {
            objectOutputStream.defaultWriteObject();
            System.out.println("write :" + name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public short getAltoActual() {
        return alto;
    }

    public void setAlto(short alto) {

        if (altoInicial == 0) {
            setAltoInicial(alto);
        }

        this.alto = alto;
    }

    public short getAnchoActual() {
        return ancho;
    }

    public void setAncho(short ancho) {
        this.ancho = ancho;
    }

//    private String formatearNombre(String nombre) {
//
//        if (nombre.startsWith("Enrutador")) {
//            return nombre.substring(0, "Enrutador".length()) + "\n" + nombre.substring("Enrutador".length() + 1, nombre.length());
//        }
//        return nombre;
//    }
    public short getCantidadDeEnlaces() {
        return cantidadDeEnlaces;
    }

    public void setCantidadDeEnlaces(short cantidadDeEnlaces) {
        this.cantidadDeEnlaces = cantidadDeEnlaces;
    }

    public boolean isInicioGeneracionDeEnlace() {
        return inicioGeneracionDeEnlace;
    }

    public void setinicioGeneracionDeEnlace(boolean inicioGeneracionDeEnlace) {
        this.inicioGeneracionDeEnlace = inicioGeneracionDeEnlace;
    }

    public String formatearNombreConSaltoDeLineas(String nombre) {

        StringBuilder nombreModificado = new StringBuilder();

        nombre = nombre.trim();
        int tamaño = nombre.length();
        int i = 0;

        while (tamaño >= pasoDeSaltoLinea) {

            nombreModificado.append(nombre.substring(i * pasoDeSaltoLinea, (i * pasoDeSaltoLinea) + pasoDeSaltoLinea)).append("\n");

            tamaño = nombre.substring(((i * pasoDeSaltoLinea) + pasoDeSaltoLinea)).length();

            if (tamaño > 0 && tamaño < pasoDeSaltoLinea) {
                nombreModificado.append(nombre.substring(((i * pasoDeSaltoLinea) + pasoDeSaltoLinea)));
            }

            i++;
        }
        setAlto((short) cuadroExteriorResaltado.getHeight());
        setAncho((short) cuadroExteriorResaltado.getWidth());
        updateNodoListener();
        return (nombreModificado.length() == 0) ? nombre : nombreModificado.toString();
    }

    public Line getEnlaceComodin() {
        return enlaceComodin;
    }

    public HashMap<String, String> getSubPropertiesNode() {
        return subPropertiesNode;
    }

    public Group getGroup() {
        return group;
    }

    public double getLayoutX() {
        return layoutX;
    }

    public void setLayoutX(double layoutX) {
        this.layoutX = layoutX;
        group.setLayoutX(layoutX);
    }

    public double getLayoutY() {
        return layoutY;
    }

    public void setLayoutY(double layoutY) {
        this.layoutY = layoutY;
        group.setLayoutY(layoutY);
    }

    private void playDenyLinkAnimation() {

        IMG_VW_DENY_LINK.setLayoutX(getLayoutX() + getAnchoActual() / 2 - IMG_VW_DENY_LINK.getBoundsInParent().getWidth() / 2);
        IMG_VW_DENY_LINK.setLayoutY(getLayoutY() + 0.75 * getAltoActual() - (getAltoInicial() / 4 + IMG_VW_DENY_LINK.getBoundsInParent().getHeight() / 2));


        grupoDeDiseno.add(IMG_VW_DENY_LINK);

        FadeTransition fadeImgDenyLink = new FadeTransition(Duration.millis(800), IMG_VW_DENY_LINK);
        fadeImgDenyLink.setFromValue(1.0);
        fadeImgDenyLink.setToValue(0);
        fadeImgDenyLink.play();

        fadeImgDenyLink.setOnFinished(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent arg0) {
                grupoDeDiseno.remove(IMG_VW_DENY_LINK);
            }
        });
    }

    public abstract boolean puedeGenerarEnlaceCon(GraphNode nodoInicioDelEnlace);
}
