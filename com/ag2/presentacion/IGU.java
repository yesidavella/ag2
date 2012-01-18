package com.ag2.presentacion;

import com.ag2.config.TipoDePropiedadesPhosphorus;
import com.ag2.config.serializacion.Serializador;
import com.ag2.presentacion.controles.Boton;
import com.ag2.presentacion.controles.GrupoDeDiseno;
import com.ag2.presentacion.controles.ResultadosPhosphorousHTML;
import com.ag2.presentacion.controles.ResultadosPhosphorus;
import com.ag2.presentacion.diseño.NodoDeRecursoGrafico;
import com.ag2.presentacion.diseño.NodoGrafico;
import com.ag2.presentacion.diseño.propiedades.TablaPropiedadesDispositivo;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class IGU extends Scene {

    GeoMap geoMap = new GeoMap();
    GrupoDeDiseno grGrupoDeDiseño = new GrupoDeDiseno();
//    Image iImagenFondo = new Image(getClass().getResourceAsStream("../../../recursos/imagenes/mapaMundi.jpg"));
//    ImageView ivImagenFondo = new ImageView(iImagenFondo);
    ToggleGroup tgHerramientas = new ToggleGroup();

    GridPane gpNavegacionMapa = new GridPane();
    ExecutePane executePane = new ExecutePane();
    private Boton btnMoverEscena;
    private Boton btnCliente = new Boton(TiposDeBoton.CLIENTE);
    Boton btnNodoDeServicio = new Boton(TiposDeBoton.NODO_DE_SERVICIO);
    Boton btnEnrutadorOptico = new Boton(TiposDeBoton.ENRUTADOR_OPTICO);
    Boton btnEnrutadorDeRafaga = new Boton(TiposDeBoton.ENRUTADOR_RAFAGA);
    Boton btnEnrutadorHibrido = new Boton(TiposDeBoton.ENRUTADOR_HIBRIDO);
    Boton btnRecurso = new Boton(TiposDeBoton.RECURSO);
    Boton btnEnlace = new Boton(TiposDeBoton.ENLACE);
    private static TiposDeBoton estadoTipoBoton = TiposDeBoton.PUNTERO;
    TablaPropiedadesDispositivo propiedadesDispositivoTbl;
    private GridPane barraHerramientas;
    private ProgressBar prgBarBarraProgresoEjec;
    private boolean estaTeclaCtrlOprimida = false;
    private TiposDeBoton estadoAnteriorDeBtnAEvento;
    private Cursor cursorAnteriorAEventoTcld;
    private ResultadosPhosphorus resultadosPhosphorus;
    private static IGU iguAG2;

    public static IGU getInstanciaIGUAg2() {

        if (iguAG2 == null) {
            iguAG2 = new IGU(new BorderPane(), 1200, 800);
        }
        return iguAG2;
    }

    private IGU(BorderPane layOutVentanaPrincipal, double anchoVentana, double altoVentana) {
        super(layOutVentanaPrincipal, anchoVentana, altoVentana);

        adicionarEventoDeTecladoAEscena(this);
        getStylesheets().add(IGU.class.getResource("../../../recursos/css/IGUPrincipal.css").toExternalForm());

        layOutVentanaPrincipal.getStyleClass().add("ventanaPrincipal");

        //Diseño superior
        crearBarraDeMenus(layOutVentanaPrincipal, Main.getStgEscenario());

        //Diseño izquierdo(contenedor de Ejecucion y herramientas)

        barraHerramientas = creacionBarraDeHerramientas();
        VBox vBoxContenedorIndicadoresEjec = crearElemsIndicadoresEjecucion();

        VBox contenedorHerramietas = new VBox();
        contenedorHerramietas.getChildren().addAll(executePane, barraHerramientas, vBoxContenedorIndicadoresEjec);
        layOutVentanaPrincipal.setLeft(contenedorHerramietas);

        //Diseño central
        TabPane cajaDetabs = crearLienzoDetabs();
        layOutVentanaPrincipal.setCenter(cajaDetabs);
        //Diseño inferior
        HBox cajaInferiorHor = crearImagenesYTablasDePropiedades();
        layOutVentanaPrincipal.setBottom(cajaInferiorHor);

        inicializarEstadoDeIGU();
    }

    public static TiposDeBoton getEstadoTipoBoton() {
        if (estadoTipoBoton == null) {
            estadoTipoBoton = TiposDeBoton.PUNTERO;
        }
        return estadoTipoBoton;
    }

    public static void setEstadoTipoBoton(TiposDeBoton tiposDeBoton) {
        estadoTipoBoton = tiposDeBoton;
    }

    private void crearBarraDeMenus(BorderPane diseñoVentana, final Stage primaryStage) {

        //Panel de menus
        HBox hBoxContenedorDeMenu = new HBox();
        hBoxContenedorDeMenu.setPadding(new Insets(3, 0, 3, 3));
        hBoxContenedorDeMenu.getStyleClass().add("contenedorDeMenus");

        //Items de menus y menus
        Menu menuArchivo = new Menu("Archivo");

        Menu menuAyuda = new Menu("Ayuda");

        MenuItem itemNuevoPrj = new MenuItem("Nuevo Proyecto");
        MenuItem itemAbrir = new MenuItem("Abrir");
        MenuItem itemGuardar = new MenuItem("Guardar");
        MenuItem itemCerrar = new MenuItem("Cerrar");

        MenuItem itemAyuda = new MenuItem("Ayuda");
        MenuItem itemAcercaDe = new MenuItem("Acerca del Proyecto AG2...");

        menuArchivo.getItems().addAll(itemNuevoPrj, new SeparatorMenuItem(), itemAbrir, itemGuardar, new SeparatorMenuItem(), itemCerrar);
        menuAyuda.getItems().addAll(itemAyuda, new SeparatorMenuItem(), itemAcercaDe);

        itemGuardar.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent t) {
                Serializador serializador = new Serializador(primaryStage);
                serializador.guardar(grGrupoDeDiseño);

            }
        });
        itemAbrir.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent t) {
                Serializador serializador = new Serializador(primaryStage);
                GrupoDeDiseno grupoDeDiseno = serializador.cargar();
                //grupoDeDiseno.getChildren().addAll(rectangle, ivImagenFondo);
                //   ivImagenFondo.toBack(); 
            }
        });

        //La barra de menus
        MenuBar mnuBarBarraDeMenus = new MenuBar();
        mnuBarBarraDeMenus.getMenus().addAll(menuArchivo, menuAyuda);
        mnuBarBarraDeMenus.getStyleClass().add("barraDeMenus");

        hBoxContenedorDeMenu.getChildren().add(mnuBarBarraDeMenus);
        diseñoVentana.setTop(hBoxContenedorDeMenu);
    }

    private GridPane creacionBarraDeHerramientas() {

        GridPane grdPnBarraDeHerramientas = new GridPane();
        grdPnBarraDeHerramientas.setPadding(new Insets(10, 10, 10, 10));
        grdPnBarraDeHerramientas.setVgap(5);
        grdPnBarraDeHerramientas.setHgap(4);
        grdPnBarraDeHerramientas.getStyleClass().add("barraDeHerramientas");

        creacionDeBtnsDeUtilidades(grdPnBarraDeHerramientas);
        creacionBtnsDeNodos(grdPnBarraDeHerramientas);

        return grdPnBarraDeHerramientas;
    }

    private void creacionDeBtnsDeUtilidades(GridPane grdPnBarraHerramientas) {

        btnMoverEscena = new Boton(TiposDeBoton.MANO);
        Boton btnSeleccion = new Boton(TiposDeBoton.PUNTERO);
        Boton btnDividirEnlaceCuadrado = new Boton(TiposDeBoton.ADICIONAR_VERTICE);
        Boton btnEliminar = new Boton(TiposDeBoton.ELIMINAR);
        Boton btnPlusZoom = new Boton(TiposDeBoton.ZOOM_PLUS);
        Boton btnMinusZoom = new Boton(TiposDeBoton.ZOOM_MINUS);

        btnMoverEscena.setGrupoDeDiseño(grGrupoDeDiseño);
        btnSeleccion.setGrupoDeDiseño(grGrupoDeDiseño);
        btnDividirEnlaceCuadrado.setGrupoDeDiseño(grGrupoDeDiseño);
        btnEliminar.setGrupoDeDiseño(grGrupoDeDiseño);
        btnPlusZoom.setGrupoDeDiseño(grGrupoDeDiseño);
        btnMinusZoom.setGrupoDeDiseño(grGrupoDeDiseño);

        btnMoverEscena.setToggleGroup(tgHerramientas);
        btnSeleccion.setToggleGroup(tgHerramientas);
        btnDividirEnlaceCuadrado.setToggleGroup(tgHerramientas);
        btnEliminar.setToggleGroup(tgHerramientas);
        btnPlusZoom.setToggleGroup(tgHerramientas);
        btnMinusZoom.setToggleGroup(tgHerramientas);

        btnMoverEscena.setTooltip(new Tooltip("Mueva el mapa a su gusto con el raton."));
        btnSeleccion.setTooltip(new Tooltip("Seleccione cualquier objeto"));
        btnDividirEnlaceCuadrado.setTooltip(new Tooltip("Añadale vertices a un enlace"));
        btnEliminar.setTooltip(new Tooltip("Elimine un objeto"));
        btnPlusZoom.setTooltip(new Tooltip("Aumente el zoom del mapa en donde realize el click"));
        btnMinusZoom.setTooltip(new Tooltip("Disminuya el zoom del mapa en donde realize el click"));

        GridPane.setConstraints(btnSeleccion, 0, 0);
        grdPnBarraHerramientas.getChildren().add(btnSeleccion);

        GridPane.setConstraints(btnMoverEscena, 1, 0);
        grdPnBarraHerramientas.getChildren().add(btnMoverEscena);

        GridPane.setConstraints(btnDividirEnlaceCuadrado, 0, 1);
        grdPnBarraHerramientas.getChildren().add(btnDividirEnlaceCuadrado);

        GridPane.setConstraints(btnEliminar, 1, 1);
        grdPnBarraHerramientas.getChildren().add(btnEliminar);
        
        GridPane.setConstraints(btnPlusZoom, 0, 2);
        grdPnBarraHerramientas.getChildren().add(btnPlusZoom);
        
        GridPane.setConstraints(btnMinusZoom, 1, 2);
        grdPnBarraHerramientas.getChildren().add(btnMinusZoom);
        
        Separator separadorHerramientas = new Separator(Orientation.HORIZONTAL);
        separadorHerramientas.getStyleClass().add("separadorBarraDeHerramientas");

        GridPane.setConstraints(separadorHerramientas, 0, 3);
        separadorHerramientas.setValignment(VPos.CENTER);

        GridPane.setColumnSpan(separadorHerramientas, 2);
        grdPnBarraHerramientas.getChildren().add(separadorHerramientas);

    }

    private void creacionBtnsDeNodos(GridPane grdPnBarraHerramientas) {

        btnCliente.setToggleGroup(tgHerramientas);
        btnNodoDeServicio.setToggleGroup(tgHerramientas);
        btnEnrutadorOptico.setToggleGroup(tgHerramientas);
        btnEnrutadorDeRafaga.setToggleGroup(tgHerramientas);
        btnEnrutadorHibrido.setToggleGroup(tgHerramientas);
        btnRecurso.setToggleGroup(tgHerramientas);
        btnEnlace.setToggleGroup(tgHerramientas);

        btnCliente.setGrupoDeDiseño(grGrupoDeDiseño);
        btnNodoDeServicio.setGrupoDeDiseño(grGrupoDeDiseño);
        btnEnrutadorOptico.setGrupoDeDiseño(grGrupoDeDiseño);
        btnEnrutadorDeRafaga.setGrupoDeDiseño(grGrupoDeDiseño);
        btnEnrutadorHibrido.setGrupoDeDiseño(grGrupoDeDiseño);
        btnRecurso.setGrupoDeDiseño(grGrupoDeDiseño);
        btnEnlace.setGrupoDeDiseño(grGrupoDeDiseño);

        btnCliente.setTooltip(new Tooltip("Nodo cliente"));
        btnNodoDeServicio.setTooltip(new Tooltip("Nodo de servicio(Middleware)"));
        btnEnrutadorOptico.setTooltip(new Tooltip("Enrutador Optico"));
        btnEnrutadorDeRafaga.setTooltip(new Tooltip("Enrutador de Ráfaga"));
        btnEnrutadorHibrido.setTooltip(new Tooltip("Enrutador Hibrido"));
        btnRecurso.setTooltip(new Tooltip("Clúster (Recurso de almacenamiento y procesamiento) "));
        btnEnlace.setTooltip(new Tooltip("Enlace Optico"));

        GridPane.setConstraints(btnCliente, 0, 4);
        grdPnBarraHerramientas.getChildren().add(btnCliente);

        GridPane.setConstraints(btnNodoDeServicio, 1, 4);
        grdPnBarraHerramientas.getChildren().add(btnNodoDeServicio);

        GridPane.setConstraints(btnEnrutadorOptico, 0, 5);
        grdPnBarraHerramientas.getChildren().add(btnEnrutadorOptico);

        GridPane.setConstraints(btnEnrutadorDeRafaga, 1, 5);
        grdPnBarraHerramientas.getChildren().add(btnEnrutadorDeRafaga);

        GridPane.setConstraints(btnEnrutadorHibrido, 0, 6);
        grdPnBarraHerramientas.getChildren().add(btnEnrutadorHibrido);

        GridPane.setConstraints(btnRecurso, 1, 6);
        grdPnBarraHerramientas.getChildren().add(btnRecurso);

        GridPane.setConstraints(btnEnlace, 0, 7);
        grdPnBarraHerramientas.getChildren().add(btnEnlace);

    }

    private TabPane crearLienzoDetabs() {

        TabPane cajaDetabs = new TabPane();

        Tab tabSimulacion = new Tab();
        tabSimulacion.setText("Simulación");
        tabSimulacion.setClosable(false);
        Tab tabResultados = new Tab();
        Tab tabResultadosHTML = new Tab();
        tabResultados.setText("Resultados Phosphorus");
        tabResultadosHTML.setText("Resultado Phosphorus HTML");

        resultadosPhosphorus = new ResultadosPhosphorus(tabResultados);
        ResultadosPhosphorousHTML resultadosPhosphorousHTML = new ResultadosPhosphorousHTML(tabResultadosHTML);


//        geoMap.setScaleX(17);
     //   geoMap.setScaleY(-1);
        grGrupoDeDiseño.getChildren().addAll(geoMap);

        tabSimulacion.setContent(grGrupoDeDiseño);

        cajaDetabs.getTabs().addAll(tabSimulacion, tabResultados, tabResultadosHTML);

        return cajaDetabs;
    }

    private HBox crearImagenesYTablasDePropiedades() {

        HBox hBoxCajaInferior = new HBox();
        hBoxCajaInferior.getStyleClass().add("cajaInferior");
        hBoxCajaInferior.setSpacing(10);

        TilePane tlPnLogos = creacionImagenesDeProyectos();

        propiedadesDispositivoTbl = new TablaPropiedadesDispositivo();

        TableView<String> tblPropiedadesSimulacion = crearTablaDePropiedadesDeSimulacion();

        SplitPane divisorDeTablas = new SplitPane();
        divisorDeTablas.getItems().addAll(propiedadesDispositivoTbl, tblPropiedadesSimulacion);

        VBox vbCajaNavegacion = new VBox();
        crearPanelDeNavegacionMapa(vbCajaNavegacion);
        hBoxCajaInferior.getChildren().addAll(tlPnLogos, divisorDeTablas, vbCajaNavegacion);
        return hBoxCajaInferior;
    }

    private TilePane creacionImagenesDeProyectos() {

        //Imagen proyecto AG2
        ImageView imagenAG2 = new ImageView(new Image(getClass().getResourceAsStream("../../../recursos/imagenes/logoAG2.png")));
        //Imagen proyecto phophorus
        ImageView imagenPhosphorus = new ImageView(new Image(getClass().getResourceAsStream("../../../recursos/imagenes/phosphorus.jpg")));
        imagenPhosphorus.setScaleX(1.4);
        imagenPhosphorus.setScaleY(1.4);

        TilePane tlPneLogos = new TilePane();
        tlPneLogos.setPrefColumns(1);
        tlPneLogos.setPadding(new Insets(5));
        tlPneLogos.setVgap(15);
        tlPneLogos.getChildren().addAll(imagenAG2, imagenPhosphorus);

        return tlPneLogos;
    }

    private TableView<String> crearTablaDePropiedadesDeSimulacion() {

        TableView<String> propiedadesSimulacionTbl = new TableView<String>();
        propiedadesSimulacionTbl.setPrefHeight(200);
        propiedadesSimulacionTbl.setPrefWidth(400);

        TableColumn tcNombrePropiedad = new TableColumn("PROPIEDAD");
        tcNombrePropiedad.setMinWidth(150);
        tcNombrePropiedad.setCellValueFactory(new PropertyValueFactory<TipoDePropiedadesPhosphorus, String>("nombrePropiedad"));
        TableColumn tcValorPropiedad = new TableColumn("VALOR");
        tcValorPropiedad.setMinWidth(250);
        tcValorPropiedad.setCellValueFactory(new PropertyValueFactory<TipoDePropiedadesPhosphorus, Control>("control"));
        TableColumn tituloSimCol = new TableColumn("PROPIEDADES SIMULACIÓN");
        tituloSimCol.getColumns().addAll(tcNombrePropiedad, tcValorPropiedad);
        propiedadesSimulacionTbl.getColumns().addAll(tituloSimCol);
        propiedadesSimulacionTbl.setItems(TipoDePropiedadesPhosphorus.getDatos());
        return propiedadesSimulacionTbl;
    }

    public void crearPanelDeNavegacionMapa(VBox vBox) {

        gpNavegacionMapa.setPadding(new Insets(10, 10, 10, 10));
        gpNavegacionMapa.setVgap(5);
        gpNavegacionMapa.setHgap(4);
        gpNavegacionMapa.getStyleClass().add("barraDeHerramientas");

        final ChoiceBox cbClientes = new ChoiceBox(grGrupoDeDiseño.getListaClientes());
        final ChoiceBox cbRecursos = new ChoiceBox(grGrupoDeDiseño.getListaRecursos());
        final ChoiceBox cbSwicthes = new ChoiceBox(grGrupoDeDiseño.getListaSwitches());
        final ChoiceBox cbNodosServicio = new ChoiceBox(grGrupoDeDiseño.getListaNodoServicio());

        cbClientes.setMinWidth(150);
        cbRecursos.setMinWidth(150);
        cbSwicthes.setMinWidth(150);
        cbNodosServicio.setMinWidth(150);

        Button btnIrClientes = new Button("ir");
        Button btnIrRecursos = new Button("ir");
        Button btnIrSwichtes = new Button("ir");
        Button btnIrNodoServicio = new Button("ir");

        Label lbRouters = new Label("Enrutadores");
        Label lbClientes = new Label("Clientes");
        Label lbRecursos = new Label("Recursos");
        Label lbNodosServicio = new Label("Agendadores");

        GridPane.setConstraints(lbClientes, 0, 2);
        gpNavegacionMapa.getChildren().add(lbClientes);
        GridPane.setConstraints(cbClientes, 1, 2);
        gpNavegacionMapa.getChildren().add(cbClientes);
        GridPane.setConstraints(btnIrClientes, 2, 2);
        gpNavegacionMapa.getChildren().add(btnIrClientes);

        GridPane.setConstraints(lbRecursos, 0, 3);
        gpNavegacionMapa.getChildren().add(lbRecursos);
        GridPane.setConstraints(cbRecursos, 1, 3);
        gpNavegacionMapa.getChildren().add(cbRecursos);
        GridPane.setConstraints(btnIrRecursos, 2, 3);
        gpNavegacionMapa.getChildren().add(btnIrRecursos);

        GridPane.setConstraints(lbRouters, 0, 4);
        gpNavegacionMapa.getChildren().add(lbRouters);
        GridPane.setConstraints(cbSwicthes, 1, 4);
        gpNavegacionMapa.getChildren().add(cbSwicthes);
        GridPane.setConstraints(btnIrSwichtes, 2, 4);
        gpNavegacionMapa.getChildren().add(btnIrSwichtes);

        GridPane.setConstraints(lbNodosServicio, 0, 5);
        gpNavegacionMapa.getChildren().add(lbNodosServicio);
        GridPane.setConstraints(cbNodosServicio, 1, 5);
        gpNavegacionMapa.getChildren().add(cbNodosServicio);
        GridPane.setConstraints(btnIrNodoServicio, 2, 5);
        gpNavegacionMapa.getChildren().add(btnIrNodoServicio);

        vBox.getChildren().add(gpNavegacionMapa);
//        establecerEventoBotonesIr(btnIrClientes, cbClientes);
//        establecerEventoBotonesIr(btnIrNodoServicio, cbNodosServicio);
//        establecerEventoBotonesIr(btnIrRecursos, cbRecursos);
//        establecerEventoBotonesIr(btnIrSwichtes, cbSwicthes);

        cbRecursos.setOnMouseClicked(new EventHandler<MouseEvent>() {

            public void handle(MouseEvent t) {
                if (cbRecursos.getSelectionModel().getSelectedItem() instanceof NodoDeRecursoGrafico) {
                    NodoDeRecursoGrafico nodoDeRecursoGrafico = (NodoDeRecursoGrafico) cbRecursos.getSelectionModel().getSelectedItem();
                }
            }
        });
    }

//    private void establecerEventoBotonesIr(Button button, final ChoiceBox cbClientes) {
//        button.setOnMouseClicked(new EventHandler<MouseEvent>() {
//
//            public void handle(MouseEvent t) {
//                if (cbClientes.getSelectionModel().getSelectedItem() instanceof NodoGrafico) {
//                    NodoGrafico nodoGrafico = (NodoGrafico) cbClientes.getSelectionModel().getSelectedItem();
//
//                    sliderZoom.setValue(100);
//                    spZonaDeDiseño.setHvalue(nodoGrafico.getLayoutX() / 25000);
//                    spZonaDeDiseño.setVvalue(nodoGrafico.getLayoutY() / 18750);
//                    grGrupoDeDiseño.setScaleX(sliderZoom.getValue() / 100);
//                    grGrupoDeDiseño.setScaleY(sliderZoom.getValue() / 100);
//                }
//            }
//        });
//    }

    private void adicionarEventoDeTecladoAEscena(final Scene escena) {

        escena.setOnKeyPressed(new EventHandler<KeyEvent>() {

            public void handle(KeyEvent event) {

                if (estaTeclaCtrlOprimida == false && event.isControlDown() && grGrupoDeDiseño.isHover()) {
                    estaTeclaCtrlOprimida = true;

                    estadoAnteriorDeBtnAEvento = IGU.getEstadoTipoBoton();
                    cursorAnteriorAEventoTcld = grGrupoDeDiseño.getCursor();

                    IGU.setEstadoTipoBoton(TiposDeBoton.MANO);
                    grGrupoDeDiseño.setCursor(TiposDeBoton.MANO.getImagenCursor());
                }
            }
        });

        escena.setOnKeyReleased(new EventHandler<KeyEvent>() {

            public void handle(KeyEvent event) {

                if (estaTeclaCtrlOprimida == true) {
                    estaTeclaCtrlOprimida = false;
                    IGU.setEstadoTipoBoton(estadoAnteriorDeBtnAEvento);
                    grGrupoDeDiseño.setCursor(cursorAnteriorAEventoTcld);
                }
            }
        });
    }

    private void inicializarEstadoDeIGU() {

        btnCliente.setSelected(true);
        IGU.setEstadoTipoBoton(TiposDeBoton.CLIENTE);
        grGrupoDeDiseño.setCursor(TiposDeBoton.CLIENTE.getImagenCursor());
    }

    public GrupoDeDiseno getGrGrupoDeDiseño() {
        return grGrupoDeDiseño;
    }

    public TablaPropiedadesDispositivo getPropiedadesDispositivoTbl() {
        return propiedadesDispositivoTbl;
    }

    private VBox crearElemsIndicadoresEjecucion() {

        VBox vBoxCajaContenedoraIndicadores = new VBox(10);
        vBoxCajaContenedoraIndicadores.getStyleClass().add("barraDeHerramientas");
        vBoxCajaContenedoraIndicadores.setPadding(new Insets(10, 10, 10, 10));
        vBoxCajaContenedoraIndicadores.setAlignment(Pos.CENTER);

        Label lblIndicadoraEjec = new Label("Ejecución:");
        lblIndicadoraEjec.setFont(new Font("Arial Bold", 10));

        prgBarBarraProgresoEjec = new ProgressBar(0);
        prgBarBarraProgresoEjec.setPrefWidth(70);
        prgBarBarraProgresoEjec.setTooltip(new Tooltip("Muestra el estado de ejecución de la simulación"));

        vBoxCajaContenedoraIndicadores.getChildren().addAll(lblIndicadoraEjec, prgBarBarraProgresoEjec);

        return vBoxCajaContenedoraIndicadores;
    }

    public void habilitar() {
        barraHerramientas.setDisable(false);
        barraHerramientas.setOpacity(1);
        prgBarBarraProgresoEjec.setProgress(0);
        IGU.setEstadoTipoBoton(estadoAnteriorDeBtnAEvento);
        grGrupoDeDiseño.setCursor(IGU.getEstadoTipoBoton().getImagenCursor());
    }

    public void deshabilitar() {
        barraHerramientas.setDisable(true);
        barraHerramientas.setOpacity(0.99);
        prgBarBarraProgresoEjec.setProgress(-1);
        estadoAnteriorDeBtnAEvento = IGU.getEstadoTipoBoton();
        IGU.setEstadoTipoBoton(TiposDeBoton.MANO);
        grGrupoDeDiseño.setCursor(IGU.getEstadoTipoBoton().getImagenCursor());
    }

    public ExecutePane getExecutePane() {
        return executePane;
    }

    public ResultadosPhosphorus getResustadosPhosphorus() {
        return resultadosPhosphorus;
    }
}